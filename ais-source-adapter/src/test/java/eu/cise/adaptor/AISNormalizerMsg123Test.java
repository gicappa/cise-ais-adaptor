package eu.cise.adaptor;

import eu.cise.adaptor.helper.TestScenario;
import eu.cise.adaptor.tbs.TBSAISNormalizer;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static eu.cise.adaptor.normalize.NavigationStatus.UnderwayUsingEngine;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// !AIVDM,1,1,,A,1`15Aq@vj:OP0BRK9L18AnUB0000,0*15
// {rateOfTurn=-4, metadata=Metadata{source='SRC', received=2018-02-15T09:52:25.049Z}, navigationStatus=UnderwayUsingEngine, trueHeading=210, latitude=47.443634, courseOverGround=211.9, positionAccuracy=false, speedOverGround=13.8, nmeaMessages=[Ldk.tbsalling.aismessages.nmea.messages.NMEAMessage;@5a106b26, sourceMmsi.MMSI=538005989, raimFlag=false, second=41, valid=true, communicationState.syncState=UTCDirect, messageType=PositionReportClassAScheduled, specialManeuverIndicator=NotAvailable, repeatIndicator=2, transponderClass=A, longitude=-6.9895167}

public class AISNormalizerMsg123Test {

    private TBSAISNormalizer n;
    private TestScenario t = new TestScenario();

    @Before
    public void before() {
        n = new TBSAISNormalizer();
    }


    @Test
    public void it_maps_position_message_type() {
        assertThat(n.translate(t.positionMsg()).getMessageType(), is(1));
    }

    @Test
    public void it_maps_voyage_message_type() {
        assertThat(n.translate(t.voyageMsg()).getMessageType(), is(5));
    }

    @Test
    public void it_maps_position_latitude() {
        assertThat(n.translate(t.positionMsg()).getLatitude(), is(47.443634F));
    }

    @Test
    public void it_maps_position_longitude() {
        assertThat(n.translate(t.positionMsg()).getLongitude(), is(-6.9895167F));
    }

    @Test
    public void it_maps_location_accuracy() {
        assertThat(n.translate(t.positionMsg()).getPositionAccuracy(), is(0));
    }

    @Test
    public void it_maps__MMSI() {
        assertThat(n.translate(t.positionMsg()).getUserId(), is(538005989));
    }

    @Test
    public void it_maps__COG() {
        assertThat(n.translate(t.positionMsg()).getCOG(), is(211.9F));
    }

    @Test
    public void it_maps__true_heading() {
        assertThat(n.translate(t.positionMsg()).getTrueHeading(), is(210));
    }

    @Test
    public void it_maps_Instant_MIN_when_timestamp_is_null() {
        assertThat(n.translate(t.positionMsg()).getTimestamp(), is(Instant.MIN));
    }

    @Test
    public void it_maps_timestamp() {
        Instant dateTime = Instant.parse("2018-02-19T14:43:16.550Z");

        assertThat(n.translate(t.positionMsgWithTime(dateTime)).getTimestamp(), is(dateTime)); // 2018-02-15T09:52:25.049Z
    }

    @Test
    public void it_maps_SOG() {
        assertThat(n.translate(t.positionMsg()).getSOG(), is(13.8F));
    }

    @Test
    public void it_maps_navigational_status() {
        assertThat(n.translate(t.positionMsg()).getNavigationStatus(), is(UnderwayUsingEngine));
    }

}
