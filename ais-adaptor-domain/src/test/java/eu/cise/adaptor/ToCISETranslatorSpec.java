package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static eu.cise.adaptor.NavigationStatus.UnderwayUsingEngine;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class ToCISETranslatorSpec {
    {
        describe("an AIS to CISE message translator", () -> {

            ToCISETranslator translator = new ToCISETranslator();

            describe("when a message type is not supported", () -> {
                asList(4, 6, 7, 8, 9, 10, 11).forEach((n) ->
                        it("returns an empty optional / " + n, () -> {
                            assertThat(translator.translate(new AISMsg.Builder(8).build()), is(Optional.empty()));
                        })
                );
            });

            describe("when a message type is 1,2,3 or 5", () -> {
                asList(1, 2, 3, 5).forEach((n) ->
                                it("returns an optional with a push message / " + n, () ->
                                        assertThat(translator.translate(new AISMsg.Builder(n).build()),
                                                is(not(Optional.empty()))))
                );
            });

            // !AIVDM,1,1,,A,1`15Aq@vj:OP0BRK9L18AnUB0000,0*15
// {rateOfTurn=-4, metadata=Metadata{source='SRC', received=2018-02-15T09:52:25.049Z}, navigationStatus=UnderwayUsingEngine, trueHeading=210, latitude=47.443634, courseOverGround=211.9, positionAccuracy=false, speedOverGround=13.8, nmeaMessages=[Ldk.tbsalling.aismessages.nmea.messages.NMEAMessage;@5a106b26, sourceMmsi.MMSI=538005989, raimFlag=false, second=41, valid=true, communicationState.syncState=UTCDirect, messageType=PositionReportClassAScheduled, specialManeuverIndicator=NotAvailable, repeatIndicator=2, transponderClass=A, longitude=-6.9895167}

// !ABVDM,2,1,2,A,5DSFVl02=s8qK8E3H00h4pLDpE=<000000000017ApB>;=qA0J11EmSP0000,0*36
// !ABVDM,2,2,2,A,00000000000,2*2D
// {toStern=18, metadata=Metadata{source='SRC', received=2018-02-15T09:52:24.986Z}, destination=DEWVN, imo.IMO=9301134, toPort=14, dataTerminalReady=false, nmeaMessages=[Ldk.tbsalling.aismessages.nmea.messages.NMEAMessage;@6e5df971, shipName=LANGENESS, sourceMmsi.MMSI=305506000, positionFixingDevice=CombinedGpsGlonass, valid=true, eta=18-07 17:00, draught=10.4, messageType=ShipAndVoyageRelatedData, toStarboard=11, callsign=V2EP6, shipType=CargoHazardousA, toBow=143, repeatIndicator=1, transponderClass=A}

            AISMsg m = new AISMsg.Builder(1)
                    .withLatitude(47.443634F)
                    .withLongitude(-6.9895167F)
                    //.withPositionAccuracy(DEFAULT)
                    .withCOG(211.9F)
                    .withTrueHeading(210)
                    .withTimestamp(Instant.now())
                    .withSOG(13.8F)
                    .withMMSI(538005989)
                    .withNavigationStatus(UnderwayUsingEngine)
                    .build();

            it("returns an optional push with a vessel", () -> {

                XmlEntityPayload payload = extractPayload(translator.translate(m));
                assertThat("The XmlEntityPayload has not been created",
                        payload, is(notNullValue()));

                List<Object> vessels = payload.getAnies();
                assertThat("There must be at least one vessel element i the payload",
                        vessels, is(not(empty())));

                assertThat("The element in the payload must be a Vessel",
                        vessels.get(0), instanceOf(Vessel.class));
            });

            it("returns an optional push with geometry", () -> {
                Vessel v = extractVessel(translator.translate(m));

                assertThat(v.getLocationRels(), is(not(empty())));

                assertThat(extractLocationRel(v).getLocation(), is(notNullValue()));

                assertThat(extractLocationRel(v).getLocation().getGeometries(), is(not(empty())));

                assertThat(extractLocationRel(v).getLocation().getGeometries().get(0), is(notNullValue()));
            });

            it("returns an optional push latitude", () -> {
                Vessel v = extractVessel(translator.translate(m));

                assertThat(extractGeometry(v).getLatitude(), is("47.443634"));
            });

            it("returns an optional push longitude", () -> {
                Vessel v = extractVessel(translator.translate(m));

                assertThat(extractGeometry(v).getLongitude(), is("-6.9895167"));
            });

            it("returns an optional push cog", () -> {
                Vessel v = extractVessel(translator.translate(m));

                assertThat(extractLocationRel(v).getCOG(), is(211.9D));
            });

            it("returns an optional push true heading", () -> {
                Vessel v = extractVessel(translator.translate(m));

                assertThat(extractLocationRel(v).getHeading(), is(210D));
            });

            it("returns an optional push heading (null for trueHeading=511)", () -> {
                AISMsg mh = new AISMsg.Builder(1)
                        .withTrueHeading(511)
                        .build();

                Vessel v = extractVessel(translator.translate(mh));

                assertThat(extractLocationRel(v).getHeading(), is(nullValue()));
            });
        });
    }

    private Objet.LocationRel extractLocationRel(Vessel v) {
        return v.getLocationRels().get(0);
    }

    private Geometry extractGeometry(Vessel v) {
        return v.getLocationRels().get(0).getLocation().getGeometries().get(0);
    }

    private Vessel extractVessel(Optional<Push> translate) {
        return (Vessel) extractPayload(translate).getAnies().get(0);
    }

    private XmlEntityPayload extractPayload(Optional<Push> m) {
        return (XmlEntityPayload) m.get().getPayload();
    }
}

