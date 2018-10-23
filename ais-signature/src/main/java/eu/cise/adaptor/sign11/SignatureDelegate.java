package eu.cise.adaptor.sign11;

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

    private Element getSignatureElement(String messageID, Document doc) {
        NodeList nodeList = doc.getElementsByTagName("Signature");

        if (nodeList.getLength() > 0) {
            return (Element) nodeList.item(0);
        } else {
            throw new AdaptorException("Signature element was not found for message with ID {"
                                               + messageID + "}");
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

    private String uriStringOf(String XSLT_PATH) throws URISyntaxException {
        return getClass().getResource(XSLT_PATH).toURI().toString();
    }

    private DigestMethod newDigestSHA1() throws NoSuchAlgorithmException,
                                                InvalidAlgorithmParameterException {
        return sigFactory.newDigestMethod(DigestMethod.SHA1, null);
    }


}
