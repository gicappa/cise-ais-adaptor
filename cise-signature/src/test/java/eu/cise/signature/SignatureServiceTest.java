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

package eu.cise.signature;

import static eu.cise.signature.Scenario.buildMessage;
import static eu.cise.signature.Scenario.file;
import static eu.cise.signature.SignatureServiceBuilder.newSignatureService;

import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.DefaultXmlValidator;
import eu.eucise.xml.XmlMapper;
import eu.eucise.xml.XmlValidator;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.Test;

public class SignatureServiceTest {

    SignatureService signature;
    Message message;
    XmlMapper xmlMapper;

    private XmlValidator xmlValidator;

    @Before
    public void before() {

        xmlMapper = new DefaultXmlMapper();
        xmlValidator = new DefaultXmlValidator();
        message = buildMessage();

        signature = newSignatureService(xmlMapper)
            .withKeyStoreName("adaptor.jks")
            .withKeyStorePassword("eucise")
            .withPrivateKeyAlias("sim1-node01.node01.eucise.fr")
            .withPrivateKeyPassword("eucise")
            .build();
    }

    @Test
    public void it_sign_and_verify_a_message() {
        Message signedMsg = signature.sign(message);

        String messageXML = xmlMapper.toXML(signedMsg);

        xmlValidator.validate(messageXML);

        signedMsg = xmlMapper.fromXML(messageXML);

        signature.verify(signedMsg);
    }

    /**
     * There was an issue on the verification of pretty printed messages because the XMLSignature
     * element was unmarshalling the xml element counting also the spaces as xml nodes.
     * <p>
     * The fix has been released in the cise-model-generator-java.
     *
     * @throws IOException        when is not able to load the xml file
     * @throws URISyntaxException when the file name has issues.
     */
    @Test
    public void it_load_an_xml_produced_by_RTI_adaptor_and_verify_it()
        throws IOException, URISyntaxException {
        String messageXML = file("/SignedPushVessels.xml");

        Message msgPretty = xmlMapper.fromXML(messageXML);

        signature.verify(msgPretty);
    }
}
