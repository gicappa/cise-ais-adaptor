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

import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static javax.xml.crypto.dsig.CanonicalizationMethod.EXCLUSIVE;
import static javax.xml.crypto.dsig.SignatureMethod.RSA_SHA1;
import static javax.xml.crypto.dsig.Transform.ENVELOPED;
import static javax.xml.crypto.dsig.Transform.XSLT;

public class SignatureDelegate {

    private final DefaultXmlMapper.NotValidating noValidationMapper
            = new DefaultXmlMapper.NotValidating();
    private final X509Certificate certificate;
    private final PrivateKey privateKey;
    private final XMLSignatureFactory sigFactory;
    private final KeySelector keySelector = new CertificateKeySelector();

    public SignatureDelegate(X509Certificate certificate, PrivateKey privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.sigFactory = buildXMLSignatureFactory();
    }

    private XMLSignatureFactory buildXMLSignatureFactory() {
        try {
            return XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        } catch (NoSuchProviderException e) {
            throw new AdaptorException("No such security provider", e);
        }
    }

    public void verifySignatureWithMessageCertificate(Message message) {
        Document doc = noValidationMapper.toDOM(message);
        verifySignature(doc);
    }

    public Message signMessageWithDelegatesPrivateKey(Message message) {
        Document unsignedDoc = noValidationMapper.toDOM(message);
        removeSignatureElementIfAny(unsignedDoc);
        Document signedDoc = signDoc(unsignedDoc);
        return noValidationMapper.fromDOM(signedDoc);
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

    private void removeSignatureElementIfAny(Document doc) {
        NodeList nodeList = doc.getElementsByTagName("Signature");

        if (nodeList.getLength() > 0) {
            Element sigElement = (Element) nodeList.item(0);
            doc.getDocumentElement().removeChild(sigElement);
        }
    }

    private Document signDoc(Document doc) {
        try {
            Reference ref = buildSignatureReference();
            SignedInfo signedInfo = buildSignedInfo(ref);
            KeyInfo keyInfo = buildKeyInfo();
            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
            XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(dsc);
            return doc;
        } catch (XMLSignatureException |
                MarshalException |
                NoSuchAlgorithmException |
                InvalidAlgorithmParameterException e) {
            throw new AdaptorException(e);
        }
    }

    private KeyInfo buildKeyInfo() {
        KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
        KeyInfo keyInfo;
        List x509Content = new ArrayList();
        x509Content.add(certificate.getSubjectDN().getName());
        x509Content.add(certificate);
        X509Data xd = keyInfoFactory.newX509Data(x509Content);
        keyInfo = keyInfoFactory.newKeyInfo(singletonList(xd));
        return keyInfo;
    }

    private SignedInfo buildSignedInfo(Reference ref) throws NoSuchAlgorithmException,
                                                             InvalidAlgorithmParameterException {
        return sigFactory.newSignedInfo(
                sigFactory.newCanonicalizationMethod(EXCLUSIVE, (C14NMethodParameterSpec) null),
                sigFactory.newSignatureMethod(RSA_SHA1, null),
                singletonList(ref)
                                       );
    }

    private Reference buildSignatureReference() throws NoSuchAlgorithmException,
                                                       InvalidAlgorithmParameterException {
        return sigFactory
                .newReference("", newDigestSHA1(),
                              asList(newTransXSLT("/identity.xslt"), newTransEnveloped()),
                              null, null);
    }

    private Transform newTransEnveloped() throws NoSuchAlgorithmException,
                                                 InvalidAlgorithmParameterException {
        return sigFactory.newTransform(ENVELOPED, (TransformParameterSpec) null);
    }

    private Transform newTransXSLT(String resource) throws NoSuchAlgorithmException,
                                                           InvalidAlgorithmParameterException {
        return sigFactory.newTransform(XSLT, new XSLTTransformParameterSpec(xsltSource(resource)));
    }

    private XMLStructure xsltSource(String xsltResource) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            Document doc = factory.newDocumentBuilder().parse(uriStringOf(xsltResource));
            return new DOMStructure(doc.getDocumentElement());
        } catch (Throwable t) {
            throw new AdaptorException(t);
        }
    }

    private String uriStringOf(String xsltPath) throws URISyntaxException {
        return getClass().getResource(xsltPath).toURI().toString();
    }

    private DigestMethod newDigestSHA1() throws NoSuchAlgorithmException,
                                                InvalidAlgorithmParameterException {
        return sigFactory.newDigestMethod(DigestMethod.SHA1, null);
    }


}
