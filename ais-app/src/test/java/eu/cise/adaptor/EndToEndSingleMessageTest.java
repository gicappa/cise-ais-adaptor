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

import static eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType.MEDIUM;
import static eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType.UNDER_WAY_USING_ENGINE;
import static java.lang.Thread.sleep;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import eu.cise.adaptor.server.TestRestServer;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.object.Objet.LocationRel;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Message;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import eu.eucise.xml.DefaultXmlMapper;
import java.util.concurrent.atomic.AtomicReference;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EndToEndSingleMessageTest {

    private AdaptorExtConfig config;
    private Thread threadMainApp;
    private TestRestServer testRestServer;
    private DefaultXmlMapper xmlMapper;
    private AtomicReference<? extends Message> atomicReference;

    /**
     * @formatter:off !AIVDM,1,1,,B,13P88o@02=OqlrHM6FATwCvf08=E,0*73
     * <p>
     * MMSI: 235014365 | Latitude: 50.854517° | Longitude: -1.348565° | Speed: 14.1 knots | Heading:
     * 127° | Course over ground: 127° | Rate of turn: 0°/min | Navigational status: 0 Nearest
     * place: Hamble, Britain (UK)
     * @formatter:on
     */

    @Before
    public void before() {
        System.setProperty("prefix.dir", "e2e-single-");
        config = ConfigFactory.create(AdaptorExtConfig.class);
        testRestServer = new TestRestServer(64738, 10);
        new Thread(testRestServer).start();
        threadMainApp = new Thread(new MainApp(config));
        xmlMapper = new DefaultXmlMapper();
        atomicReference = new AtomicReference<>();
    }

    @Test
    public void it_deserialize_a_message_from_a_file() {
        try {
            threadMainApp.start();
            testRestServer.checkRequest(r -> atomicReference.set(xmlMapper.fromXML(r)));
            threadMainApp.join(2000);

            sleep(2);

            Vessel vessel = extractVessel(atomicReference.get());

            assertThat(locationRelInfo(vessel).getCOG(), is(127.7D));
            assertThat(locationRelInfo(vessel).getSOG(), is(14.1D));
            assertThat(vessel.getMMSI(), is(235014365L));
            assertThat(vessel.getNavigationalStatus(), is(UNDER_WAY_USING_ENGINE));
            assertThat(locationRelInfo(vessel).getHeading(), is(127D));
            assertThat(geometryInfo(vessel).getLatitude(), is("50.854515")); //50.8545167
            assertThat(geometryInfo(vessel).getLongitude(), is("-1.3485667")); //1.3485667
            assertThat(locationInfo(vessel).getLocationQualitativeAccuracy(), is(MEDIUM));
            // System.out.println(locationRelInfo(vessel).getPeriodOfTime()); // To be checked


        } catch (InterruptedException e) {
            testRestServer.shutdown();
            fail("An exception occurred");
        }
    }

    private Geometry geometryInfo(Vessel vessel) {
        return locationInfo(vessel).getGeometries().get(0);
    }

    private Location locationInfo(Vessel vessel) {
        return locationRelInfo(vessel).getLocation();
    }

    private LocationRel locationRelInfo(Vessel vessel) {
        return vessel.getLocationRels().get(0);
    }

    private Vessel extractVessel(Message message) {
        return (Vessel) ((XmlEntityPayload) message.getPayload()).getAnies().get(0);
    }

    @After
    public void after() {
        testRestServer.shutdown();
    }
}