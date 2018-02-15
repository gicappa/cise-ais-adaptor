package eu.cise.adaptor;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.tbsalling.Normalizer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// !AIVDM,1,1,,A,1`15Aq@vj:OP0BRK9L18AnUB0000,0*15
// {rateOfTurn=-4, metadata=Metadata{source='SRC', received=2018-02-15T09:52:25.049Z}, navigationStatus=UnderwayUsingEngine, trueHeading=210, latitude=47.443634, courseOverGround=211.9, positionAccuracy=false, speedOverGround=13.8, nmeaMessages=[Ldk.tbsalling.aismessages.nmea.messages.NMEAMessage;@5a106b26, sourceMmsi.MMSI=538005989, raimFlag=false, second=41, valid=true, communicationState.syncState=UTCDirect, messageType=PositionReportClassAScheduled, specialManeuverIndicator=NotAvailable, repeatIndicator=2, transponderClass=A, longitude=-6.9895167}

// !ABVDM,2,1,2,A,5DSFVl02=s8qK8E3H00h4pLDpE=<000000000017ApB>;=qA0J11EmSP0000,0*36
// !ABVDM,2,2,2,A,00000000000,2*2D
// {toStern=18, metadata=Metadata{source='SRC', received=2018-02-15T09:52:24.986Z}, destination=DEWVN, imo.IMO=9301134, toPort=14, dataTerminalReady=false, nmeaMessages=[Ldk.tbsalling.aismessages.nmea.messages.NMEAMessage;@6e5df971, shipName=LANGENESS, sourceMmsi.MMSI=305506000, positionFixingDevice=CombinedGpsGlonass, valid=true, eta=18-07 17:00, draught=10.4, messageType=ShipAndVoyageRelatedData, toStarboard=11, callsign=V2EP6, shipType=CargoHazardousA, toBow=143, repeatIndicator=1, transponderClass=A}

public class ToInternalAISMessageTest {

    private Normalizer translator;

    @Before
    public void before() {
        translator = new Normalizer();
    }


    @Test
    public void it_maps_position_message_type() {
        assertThat(translator.normalize(positionMsg()).getMessageType(), is(1));
    }

    @Test
    public void it_maps_voyage_message_type() {
        assertThat(translator.normalize(voyageMsg()).getMessageType(), is(5));
    }

    @Test
    public void it_maps_position_latitude() {
        assertThat(translator.normalize(positionMsg()).getLatitude(), is(47.443634F));
    }

    @Test
    public void it_maps_position_longitude() {
        assertThat(translator.normalize(positionMsg()).getLongitude(), is(-6.9895167F));
    }

    @Test
    public void it_maps_position_MMSI() {
        assertThat(translator.normalize(positionMsg()).getMMSI(), is(538005989));
    }

    @Test
    public void it_maps_position_COG() {
        assertThat(translator.normalize(positionMsg()).getCOG(), is(211.9F));
    }

    @Test
    public void it_maps_position_true_heading() {
        assertThat(translator.normalize(positionMsg()).getTrueHeading(), is(210));
    }

    private AISMessage positionMsg() {
        return AISMessage.create(
                NMEAMessage.fromString("!AIVDM,1,1,,A,1`15Aq@vj:OP0BRK9L18AnUB0000,0*15")
        );
    }

    private AISMessage voyageMsg() {
        return AISMessage.create(
                NMEAMessage.fromString("!ABVDM,2,1,2,A,5DSFVl02=s8qK8E3H00h4pLDpE=<000000000017ApB>;=qA0J11EmSP0000,0*36"),
                NMEAMessage.fromString("!ABVDM,2,2,2,A,00000000000,2*2D")
        );
    }
}
