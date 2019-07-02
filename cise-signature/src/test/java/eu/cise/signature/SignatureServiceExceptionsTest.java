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

package eu.cise.signature;

import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Message;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import eu.cise.signature.certificates.DefaultCertificateRegistry;
import eu.cise.signature.certificates.KeyStoreInfo;
import eu.cise.signature.exceptions.InvalidMessageSignatureEx;
import eu.cise.signature.exceptions.SigningCACertInvalidSignatureEx;
import eu.cise.signature.verifiers.DefaultDomVerifier;
import eu.eucise.xml.DefaultXmlMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static eu.cise.signature.Scenario.buildMessage;
import static eu.cise.signature.Scenario.file;
import static eu.cise.signature.SignatureServiceBuilder.newSignatureService;

public class SignatureServiceExceptionsTest extends SignatureServiceTest {

    private SignatureService otherSignature;

    @Before
    public void before() {
        super.before();

        otherSignature = newSignatureService(xmlMapper)
                .withKeyStoreName("adaptor_other_fr_ca.jks")
                .withKeyStorePassword("eucise")
                .withPrivateKeyAlias("sim1-node01.node01.eucise.fr")
                .withPrivateKeyPassword("eucise")
                .build();
    }


    @Test(expected = InvalidMessageSignatureEx.class)
    public void it_fails_if_message_id_is_tampered() {
        Message signedMsg = signature.sign(message);

        // tampering the messageID
        signedMsg.setMessageID("tampered-messageID");

        signature.verify(signedMsg);
    }

    @Test(expected = InvalidMessageSignatureEx.class)
    public void it_fails_if_payload_is_tampered() {
        Message signedMsg = signature.sign(buildMessage());

        // Tampering the messagePayload
        extractVesselPayload(signedMsg).setCallSign("tampered-payload");

        signature.verify(signedMsg);
    }

    @Test(expected = SigningCACertInvalidSignatureEx.class)
    public void it_fails_if_the_message_is_signed_but_the_signing_ca_is_wrong() {
        Message signedMsg = signature.sign(buildMessage());

        otherSignature.verify(signedMsg);
    }

    @Test
    @Ignore
    public void it_verify_the_signature_produced_by_a_LightClient() throws Exception {
        String messageXML = file("/SignedPushVessels.xml");
        Message signedMsg = new DefaultXmlMapper.PrettyNotValidating().fromXML(messageXML);

        signature.verify(signedMsg);
    }

    private Vessel extractVesselPayload(Message signedMsg) {
        return (Vessel) ((XmlEntityPayload) signedMsg.getPayload()).getAnies().get(0);
    }
}
