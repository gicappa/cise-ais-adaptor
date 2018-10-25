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

package eu.cise.adaptor.signature;

import eu.cise.adaptor.CertificateRegistry;
import eu.cise.adaptor.DomVerifier;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.security.x509.X500Name;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.function.Consumer;

import static eu.cise.adaptor.exceptions.ExceptionHandler.safe;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.xpath.XPathConstants.NODE;

public class DefaultDomVerifier implements DomVerifier {

    private static final String X_PATH_TO_CERTIFICATE
            = "//*[local-name() = 'KeyInfo']/*[local-name() = 'X509Data']/*[local-name() = " +
            "'X509Certificate']";
    private final XmlMapper xmlMapper = new DefaultXmlMapper.NotValidating();
    private final XMLSignatureFactory sigFactory;
    private final KeySelector keySelector;
    private final XPathExpression certXPath;
    private final CertificateRegistry registry;

    public DefaultDomVerifier(CertificateRegistry registry) {
        this.sigFactory = buildXMLSignatureFactory();
        this.registry = registry;
        this.keySelector = new CertificateKeySelector();
        this.certXPath = compileXPath(X_PATH_TO_CERTIFICATE);
    }

    @Override
    public void verify(Document document) {
//        Document doc = xmlMapper.toDOM(message);
        verifySignature(document);
//        verifyCertificateAgainstCACert(message);
    }

    private XPathExpression compileXPath(String xPathCertificate) {
        return safe(() -> XPathFactory.newInstance().newXPath().compile(xPathCertificate));
    }


    private void verifySignature(Document doc) {
        applyToSignature(doc, (node) -> {
            try {

                DOMValidateContext valCtx = new DOMValidateContext(keySelector, node);

                XMLSignature sig = sigFactory.unmarshalXMLSignature(valCtx);

                if (!sig.validate(valCtx)) {
                    throw new AdaptorException("Signature verification failed.");
                }
            } catch (MarshalException e) {
                throw new AdaptorException("Marshalling error", e);
            } catch (XMLSignatureException e) {
                throw new AdaptorException("Signature verification error", e);
            }

        });
    }

    private void applyToSignature(Document doc, Consumer<Node> consumer) {
        NodeList nodeList = doc.getElementsByTagName("Signature");

        if (nodeList.getLength() <= 0) {
            throw new AdaptorException("Signature element was not found.");
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            consumer.accept(nodeList.item(i));
        }
    }

    private void verifyCertificateAgainstCACert(Message message) {
        try {
            X509Certificate certificate =
                    parseBase64Certificate(
                            addBeginEndToCertificate(
                                    removeCarriageReturn(
                                            extractCertificateText(
                                                    getCertificateElement(message)))));

            String issuerCertNameInJKS = extractIssuerNameFrom(certificate);

            X509Certificate caCert = registry.findPublicCertificate(issuerCertNameInJKS);

            certificate.verify(caCert.getPublicKey());

        } catch (Exception e) {
            throw new AdaptorException("Exception at certificate verification for message with ID" +
                                               " {" + message.getMessageID() + "}", e);
        }
    }

    private String extractIssuerNameFrom(X509Certificate certificate) throws IOException {
        return ((X500Name) certificate.getIssuerDN())
                .getOrganization().replace("eu.cise.", "")
                .replace(' ', '-')
                .toLowerCase() + ".cert";
    }

    private X509Certificate parseBase64Certificate(String certText) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certText.getBytes(UTF_8)));
    }

    private String removeCarriageReturn(String text) {
        return text.replace("\n", "");
    }

    private String extractCertificateText(Element certificateElement) {
        return certificateElement.getFirstChild().getNodeValue();
    }

    private Element getCertificateElement(Message message) throws XPathExpressionException {
        return (Element) certXPath.evaluate(message.getAny(), NODE);
    }

    private String addBeginEndToCertificate(String certBase64) {
        return "-----BEGIN CERTIFICATE-----\n" + certBase64 + "\n-----END CERTIFICATE-----";
    }

    private XMLSignatureFactory buildXMLSignatureFactory() {
        try {
            return XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        } catch (NoSuchProviderException e) {
            throw new AdaptorException("No such security provider", e);
        }
    }

}
