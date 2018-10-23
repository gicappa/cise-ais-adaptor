/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
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

import eu.cise.adaptor.server.TestRestServer;
import eu.eucise.xml.DefaultXmlMapper;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EndToEndConversionTest {

    private CertificateConfig config;
    private Thread threadMainApp;
    private TestRestServer testRestServer;
    private DefaultXmlMapper xmlMapper;

    @Before
    public void before() {
        config = ConfigFactory.create(CertificateConfig.class);
        testRestServer = new TestRestServer(64738, 10);
        new Thread(testRestServer).start();
        threadMainApp = new Thread(new MainApp(config));
        xmlMapper = new DefaultXmlMapper();
    }

    @Test
    public void it_deserialize_a_message_from_a_file() {
        try {
            threadMainApp.start();
            testRestServer.checkRequest(r-> xmlMapper.fromXML(r));
            threadMainApp.join(30000);

            sleep(5);

            assertEquals(96, testRestServer.countInvocations());
        } catch (InterruptedException e) {
            testRestServer.shutdown();
            fail("An exception occurred");
        }
    }

    @After
    public void after() {
        testRestServer.shutdown();
    }
}
