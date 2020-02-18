/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.adaptor;

import eu.cise.adaptor.AdaptorLogger.Slf4j;
import eu.cise.adaptor.helper.TestScenario;
import eu.cise.adaptor.translate.AisMessageToAisMsg;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static eu.cise.adaptor.translate.utils.NavigationStatus.UnderwayUsingEngine;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// !AIVDM,1,1,,A,1`15Aq@vj:OP0BRK9L18AnUB0000,0*15
// {rateOfTurn=-4, metadata=Metadata{source='SRC', received=2018-02-15T09:52:25.049Z}, navigationStatus=UnderwayUsingEngine, trueHeading=210, latitude=47.443634, courseOverGround=211.9, positionAccuracy=false, speedOverGround=13.8, nmeaMessages=[Ldk.tbsalling.aismessages.nmea.messages.NMEAMessage;@5a106b26, sourceMmsi.MMSI=538005989, raimFlag=false, second=41, valid=true, communicationState.syncState=UTCDirect, messageType=PositionReportClassAScheduled, specialManeuverIndicator=NotAvailable, repeatIndicator=2, transponderClass=A, longitude=-6.9895167}

public class AisNormalizerMsg123Test {

    private AisMessageToAisMsg n;
    private TestScenario t = new TestScenario();

    @Before
    public void before() {
        n = new AisMessageToAisMsg(new Slf4j());
    }

    @Test
    public void it_maps_position_message_type() {
        assertThat(n.translate(t.positionMsg()).get().getMessageType(), is(1));
    }

    @Test
    public void it_maps_voyage_message_type() {
        assertThat(n.translate(t.voyageMsg()).get().getMessageType(), is(5));
    }

    @Test
    public void it_maps_position_latitude() {
        assertThat(n.translate(t.positionMsg()).get().getLatitude(), is(47.443634F));
    }

    @Test
    public void it_maps_position_longitude() {
        assertThat(n.translate(t.positionMsg()).get().getLongitude(), is(-6.9895167F));
    }

    @Test
    public void it_maps_location_accuracy() {
        assertThat(n.translate(t.positionMsg()).get().getPositionAccuracy(), is(0));
    }

    @Test
    public void it_maps_MMSI() {
        assertThat(n.translate(t.positionMsg()).get().getUserId(), is(538005989));
    }

    @Test
    public void it_maps_COG() {
        assertThat(n.translate(t.positionMsg()).get().getCOG(), is(211.9F));
    }

    @Test
    public void it_maps_true_heading() {
        assertThat(n.translate(t.positionMsg()).get().getTrueHeading(), is(210));
    }

    @Test
    public void it_maps_Instant_MIN_when_timestamp_is_null() {
        assertThat(n.translate(t.positionMsg()).get().getTimestamp(), is(Instant.MIN));
    }

    @Test
    public void it_maps_timestamp() {
        Instant dateTime = Instant.parse("2018-02-19T14:43:16.550Z");

        assertThat(n.translate(t.positionMsgWithTime(dateTime)).get().getTimestamp(), is(dateTime)); // 2018-02-15T09:52:25.049Z
    }

    @Test
    public void it_maps_SOG() {
        assertThat(n.translate(t.positionMsg()).get().getSOG(), is(13.8F));
    }

    @Test
    public void it_maps_navigational_status() {
        assertThat(n.translate(t.positionMsg()).get().getNavigationStatus(), is(UnderwayUsingEngine));
    }

}
