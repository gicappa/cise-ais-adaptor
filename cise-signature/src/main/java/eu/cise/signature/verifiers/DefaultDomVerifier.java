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

package eu.cise.signature.verifiers;

import eu.cise.signature.CertificateRegistry;
import eu.cise.signature.DomVerifier;
import eu.cise.signature.exceptions.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;
import java.util.function.Consumer;

import static eu.cise.signature.exceptions.ExceptionHandler.safe;
import static javax.xml.xpath.XPathConstants.NODE;

public class DefaultDomVerifier implements DomVerifier {

    private static final String X_PATH_TO_CERTIFICATE
            = "//*[local-name() = 'KeyInfo']/*[local-name() = 'X509Data']/*[local-name() = " +
            "'X509Certificate']";

    private final XMLSignatureFactory sigFactory;
    private final KeySelector keySelector;
    private final CertificateRegistry registry;
    private final eu.cise.signature.verifiers.CertUtils CertUtils;

    public DefaultDomVerifier(CertificateRegistry registry) {
        this.sigFactory = buildXMLSignatureFactory();
        this.registry = registry;
        this.keySelector = new CertificateKeySelector();
        this.CertUtils = new CertUtils();
    }

    /**
     * Enclosed in a message it may be found a certificate of the adaptor
     * that signed the message and the signature itself.
     * <p>
     * Therefore this method verifies two signatures:
     *
     * 1. The signature contained in the message with the public key contained
     *    in the certificate of the adaptor included in the cise message.
     *
     * 2. The validity and signature of the certificate coming from the adaptor
     *    and included in the message with the public key contained in the
     *    signing ca certificate, that will be taken by the keystore local to
     *    the node.
     *
     * @param document the dom document containing the signature element
     */
    @Override
    public void verify(Document document) {
        verifyMessageSignature(document);
        verifyMessageCertWithCACert(document);
    }

    private void verifyMessageSignature(Document doc) {
        forEachSignatureIn(doc, (node) -> {
            try {
                DOMValidateContext validationContext = new DOMValidateContext(keySelector, node);
                XMLSignature sig = sigFactory.unmarshalXMLSignature(validationContext);

                if (!sig.validate(validationContext)) {
                    throw new InvalidMessageSignatureEx();
                }

            } catch (MarshalException e) {
                throw new SignatureMarshalEx(e);
            } catch (XMLSignatureException e) {
                throw new InvalidMessageSignatureEx(e);
            }

        });
    }

    /**
     * Validate the certificate provided by the message sender against the one that should
     * have signed it (the signing-ca) that is already present in the Java Key Store.
     *
     * @param document that contains the certificate provided by the message sender
     */
    private void verifyMessageCertWithCACert(Document document) {
        try {
            X509Certificate messageCert =
                    CertUtils.stringToX509Cert(getCertString(getCertElement(document)));

            String signingCAName = extractSigningCAFrom(messageCert);

            X509Certificate signingCA = registry.findCertificate(signingCAName);

            messageCert.verify(signingCA.getPublicKey());

        } catch (Exception e) {
            throw new SigningCACertInvalidSignatureEx(e);
        }
    }

    private void forEachSignatureIn(Document doc, Consumer<Node> consumer) {
        NodeList nodeList = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

        if (nodeList.getLength() <= 0) throw new SignatureNotPresentEx();

        for (int i = 0; i < nodeList.getLength(); i++) {
            consumer.accept(nodeList.item(i));
        }
    }

    private Element getCertElement(Document document) throws XPathExpressionException {
        return (Element) xpathToCert().evaluate(document, NODE);
    }

    private String getCertString(Element certificateElement) {
        return certificateElement.getFirstChild().getNodeValue();
    }

    private XPathExpression xpathToCert() {
        return safe(() -> XPathFactory.newInstance().newXPath().compile(X_PATH_TO_CERTIFICATE));
    }


    /**
     * This method extract the signing-ca certificate name. The name of the signing-ca is contained
     * in the issuer of the certificate.
     *
     * @param certificate from where it will be extracted the signing-ca
     * @return the name of the singing-ca
     */
    private String extractSigningCAFrom(X509Certificate certificate) {
        try {
            return new LdapName(certificate.getIssuerDN().getName()).getRdns().stream()
                    .filter(rdn -> rdn.getType().equalsIgnoreCase("CN"))
                    .map(Rdn::getValue)
                    .map(Object::toString)
                    .findFirst()
                    .orElseThrow(MessageCertInvalidIssuerEx::new);

        } catch (InvalidNameException e) {
            throw new MessageCertInvalidIssuerEx(e);
        }
    }

    private XMLSignatureFactory buildXMLSignatureFactory() {
        try {
            return XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        } catch (NoSuchProviderException e) {
            throw new SignatureEx("No such security provider", e);
        }
    }

}
