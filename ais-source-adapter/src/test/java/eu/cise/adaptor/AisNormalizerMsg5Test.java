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

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.translate.AisMessageToAisMsg;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * The two following ais messages are unmarshalled with the following values
 * <pre>
 * !ABVDM,2,1,2,A,5DSFVl02=s8qK8E3H00h4pLDpE=<000000000017ApB>;=qA0J11EmSP0000,0*36
 * !ABVDM,2,2,2,A,00000000000,2*2D
 * {
 *  toStern: 18,
 *  metadata: {
 *      source: 'SRC',
 *      received: '2018-02-15T09:52:24.986Z'
 *  },
 *  destination: 'DEWVN',
 *  imo.IMO: 9301134,
 *  toPort: 14,
 *  dataTerminalReady: false,
 *  nmeaMessages: [{
 *      shipName: 'LANGENESS',
 *      sourceMmsi.MMSI: 305506000,
 *      positionFixingDevice: 'CombinedGpsGlonass',
 *      valid: true,
 *      eta: '18-07 17:00',
 *      draught: 10.4,
 *      messageType: 'ShipAndVoyageRelatedData',
 *      toStarboard: 11,
 *      callsign: 'V2EP6',
 *      shipType: 'CargoHazardousA',
 *      toBow: 143,
 *      repeatIndicator: 1,
 *      transponderClass: 'A'
 *  }]
 * }
 * </pre>
 * for all month, day, hours, minutes.
 */
public class AisNormalizerMsg5Test {

    private AisMessageToAisMsg n;

    private AISMessage voyageMsg() {
        return AISMessage.create(
                NMEAMessage.fromString(
                        "!ABVDM,2,1,2,A,5DSFVl02=s8qK8E3H00h4pLDpE=<000000000017ApB>;" +
                                "=qA0J11EmSP0000,0*36"),
                NMEAMessage.fromString(
                        "!ABVDM,2,2,2,A,00000000000,2*2D")
                                );
    }

    private AISMessage voyageMsgNoMonthDay() {
        return AISMessage.create(
                NMEAMessage.fromString(
                        "!AIVDM,2,1,8,B,56?cVR42>RBT5HAJ220<N3;" +
                                "3:22222222222220n5Hk7540Ht02CQ2@H8888,0*66"),
                NMEAMessage.fromString(
                        "!AIVDM,2,2,8,B,88888888880,2*2F")
                                );
    }

    @Before
    public void before() {
        n = new AisMessageToAisMsg();
    }

    @Test
    public void it_maps_voyage_message_type() {
        assertThat(n.translate(voyageMsg()).getMessageType(), is(5));
    }

    @Test
    public void it_maps_voyage_message_destination() {
        assertThat(n.translate(voyageMsg()).getDestination(), is("DEWVN"));
    }

    // ETA:
    //    Estimated time of arrival; MMDDHHMM UTC
    //    Bits 19-16: month; 1-12; 0 = not available = default
    //    Bits 15-11: day; 1-31; 0 = not available = default
    //    Bits 10-6: hour; 0-23; 24 = not available = default
    //    Bits 5-0: minute; 0-59; 60 = not available = default
    @Test
    // eta=18-07 17:00
    public void it_maps_voyage_message_ETA_on_the_current_year() {
        Clock beforeJuly2018
                = Clock.fixed(Instant.parse("2018-05-18T17:00:00.00Z"), ZoneId.of("UTC"));

        n = new AisMessageToAisMsg(beforeJuly2018);

        assertThat(n.translate(voyageMsg()).getEta(), is(Instant.parse("2018-07-18T17:00:00.00Z")));
    }

    //00-00 24:60
    @Test
    public void it_maps_voyage_message_ETA_on_null_when_month_and_day_are_not_available() {
        assertThat(n.translate(voyageMsgNoMonthDay()).getEta(), is(nullValue()));
    }

    @Test
    public void it_maps_voyage_message_imo_number() {
        assertThat(n.translate(voyageMsg()).getImoNumber(), is(9301134));
    }

    @Test
    public void it_maps_voyage_message_call_sign() {
        assertThat(n.translate(voyageMsg()).getCallSign(), is("V2EP6"));
    }

    @Test
    public void it_maps_voyage_message_draught() {
        assertThat(n.translate(voyageMsg()).getDraught(), is(10.4F));
    }

    @Test
    public void it_maps_voyage_message_dimension_C() {
        assertThat(n.translate(voyageMsg()).getDimensionC(), is(14));
    }

    @Test
    public void it_maps_voyage_message_dimension_D() {
        assertThat(n.translate(voyageMsg()).getDimensionD(), is(11));
    }

    @Test
    public void it_maps_voyage_message_dimension_A() {
        assertThat(n.translate(voyageMsg()).getDimensionA(), is(143));
    }

    @Test
    public void it_maps_voyage_message_dimension_B() {
        assertThat(n.translate(voyageMsg()).getDimensionB(), is(18));
    }

    @Test
    //CargoHazardousA(71)
    public void it_maps_voyage_message_ship_type() {
        assertThat(n.translate(voyageMsg()).getShipType(), is(71));
    }

    @Test
    public void it_maps_voyage_message_ship_name() {
        assertThat(n.translate(voyageMsg()).getShipName(), is("LANGENESS"));
    }
}
