package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AISTranslator;
import eu.cise.adaptor.translate.DefaultAISTranslator;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static eu.cise.adaptor.normalize.NavigationStatus.UnderwayUsingEngine;
import static eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType.HIGH;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class TranslatorSpec {
    {
        describe("an AIS to CISE message translator", () -> {

            AISAdaptorConfig config = ConfigFactory.create(AISAdaptorConfig.class);
            AISTranslator translator = new DefaultAISTranslator(config);

            describe("when a message type is not supported", () -> {
                asList(4, 6, 7, 8, 9, 10, 11).forEach((n) ->
                        it("returns an empty optional / " + n, () -> {
                            assertThat(translator.translate(new AISMsg.Builder(8).build()), is(Optional.empty()));
                        })
                );
            });

            final AISMsg m = new AISMsg.Builder(1)
                    .withLatitude(47.443634F)
                    .withLongitude(-6.9895167F)
                    .withPositionAccuracy(1)
                    .withCOG(2119.0F)
                    .withTrueHeading(210)
                    .withTimestamp(Instant.parse("2018-02-19T14:43:16.550Z"))
                    .withSOG(138.0F)
                    .withMMSI(538005989)
                    .withNavigationStatus(UnderwayUsingEngine)
                    .build();

            describe("when a message type is 1,2,3 or 5", () -> {
                final Vessel v = extractVessel(translator.translate(m));

                asList(1, 2, 3, 5).forEach((n) ->
                        it("returns an optional with a push message / " + n, () ->
                                assertThat(translator.translate(new AISMsg.Builder(n).build()),
                                        is(not(Optional.empty()))))
                );

                it("returns an Optional<Push> with a vessel", () -> {

                    XmlEntityPayload payload = extractPayload(translator.translate(m));
                    assertThat("The XmlEntityPayload has not been created",
                            payload, is(notNullValue()));

                    List<Object> vessels = payload.getAnies();
                    assertThat("There must be at least one vessel element i the payload",
                            vessels, is(not(empty())));

                    assertThat("The element in the payload must be a Vessel",
                            vessels.get(0), instanceOf(Vessel.class));
                });

                it("returns an Optional<Push> with geometry", () -> {
                    assertThat(v.getLocationRels(), is(not(empty())));

                    assertThat(extractLocationRel(v).getLocation(), is(notNullValue()));

                    assertThat(extractLocationRel(v).getLocation().getGeometries(), is(not(empty())));

                    assertThat(extractLocationRel(v).getLocation().getGeometries().get(0), is(notNullValue()));
                });

                it("returns an Optional<Push> latitude", () -> {
                    assertThat(extractGeometry(v).getLatitude(), is("47.443634"));
                });

                it("returns an Optional<Push> longitude", () -> {
                    assertThat(extractGeometry(v).getLongitude(), is("-6.9895167"));
                });

                it("returns an Optional<Push> location qualitative  accuracy", () -> {
                    assertThat(extractLocationRel(v).getLocation().getLocationQualitativeAccuracy(), is(HIGH));
                });

                it("returns an Optional<Push> cog (in degrees instead of 1/10 od degrees)", () -> {
                    assertThat(extractLocationRel(v).getCOG(), is(211.9D));
                });

                it("returns an Optional<Push> cog (null for cog=3600)", () -> {
                    AISMsg mc = new AISMsg.Builder(1)
                            .withCOG(3600f)
                            .build();

                    Vessel vc = extractVessel(translator.translate(mc));

                    assertThat(extractLocationRel(vc).getCOG(), is(nullValue()));
                });

                it("returns an Optional<Push> true heading", () -> {
                    assertThat(extractLocationRel(v).getHeading(), is(210D));
                });

                it("returns an Optional<Push> heading (null for trueHeading=511)", () -> {
                    AISMsg mh = new AISMsg.Builder(1)
                            .withTrueHeading(511)
                            .build();

                    Vessel vh = extractVessel(translator.translate(mh));

                    assertThat(extractLocationRel(vh).getHeading(), is(nullValue()));
                });

                it("returns an Optional<Push> source type", () -> {
                    assertThat(extractLocationRel(v).getSourceType(), is(SourceType.DECLARATION));
                });

                it("returns an Optional<Push> sensor type", () -> {
                    assertThat(extractLocationRel(v).getSensorType(), is(SensorType.AUTOMATIC_IDENTIFICATION_SYSTEM));
                });

                it("returns an Optional<Push> sog (in knots instead of 1/10th of knots)", () -> {
                    assertThat(extractLocationRel(v).getSOG(), is(13.8D));
                });

                it("returns an Optional<Push> sog (null for SOG=1023)", () -> {
                    AISMsg ms = new AISMsg.Builder(1)
                            .withSOG(1023f)
                            .build();

                    Vessel vs = extractVessel(translator.translate(ms));
                    assertThat(extractLocationRel(vs).getSOG(), is(nullValue()));
                });

                it("returns an Optional<Push> periodOfTime.startDate", () -> {
                    //"2018-02-19T14:43:16.550Z"
                    assertThat(extractLocationRel(v).getPeriodOfTime().getStartDate(),
                            is(xmlDate(2018, 02, 19)));
                });

                it("returns an Optional<Push> periodOfTime.startTime", () -> {
                    //"2018-02-19T14:43:16.550Z"
                    assertThat(extractLocationRel(v).getPeriodOfTime().getStartTime(),
                            is(xmlTime(14, 43, 16)));
                });

                it("returns an Optional<Push> MMSI", () -> {
                    assertThat(v.getMMSI(), is(538005989L));
                });

                it("returns an Optional<Push> navigationStatus", () -> {
                    assertThat(v.getNavigationalStatus(), is(NavigationalStatusType.UNDER_WAY_USING_ENGINE));
                });
            });

        });
    }

    private XMLGregorianCalendar xmlDate(int year, int month, int day)
            throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(year, month, day,
                        0, 0, 0, 0, 0);
    }

    private XMLGregorianCalendar xmlTime(int hour, int minute, int second)
            throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(1970, 1, 1,
                        hour, minute, second, 0, 0);
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

