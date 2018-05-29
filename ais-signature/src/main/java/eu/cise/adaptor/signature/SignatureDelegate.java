package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignatureDelegate {

    private final DefaultXmlMapper.NotValidating noValidationMapper = new DefaultXmlMapper.NotValidating();
    private final X509Certificate certificate;
    private final PrivateKey privateKey;
    private final XMLSignatureFactory signatureFactory;
    private final KeySelector keySelector = new CertificateKeySelector();

    public SignatureDelegate(X509Certificate certificate, PrivateKey privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.signatureFactory = buildXMLSignatureFactory();
    }

    private XMLSignatureFactory buildXMLSignatureFactory() {
        try {
            return XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        } catch (NoSuchProviderException e) {
            throw new AISAdaptorException("No such security provider", e);
        }
    }

    public void verifySignatureWithMessageCertificate(Message message) {
        Document doc = noValidationMapper.toDOM(message);
        verifySignatureForDocument(message.getMessageID(), doc);
    }

    public Message signMessageWithDelegatesPrivateKey(Message message) {
        Document unsignedDoc = noValidationMapper.toDOM(message);
        removeSignatureElementIfAny(unsignedDoc);
        Document signedDoc = signDoc(unsignedDoc);
        Message outMessage = noValidationMapper.fromDOM(signedDoc);
        return outMessage;
    }

    private void verifySignatureForDocument(String messageID, Document doc) {
        Element sigEl = getSignatureElement(messageID, doc);
        try {
            DOMValidateContext valCtx = new DOMValidateContext(keySelector, sigEl);
            XMLSignature sig = signatureFactory.unmarshalXMLSignature(valCtx);
            if (!sig.validate(valCtx)) {
                throw new AISAdaptorException("Signature verification failed for message with ID {"
                        + messageID + "}");
            }
        } catch (MarshalException e) {
            throw new AISAdaptorException("Signature element was not found for message with ID {"
                    + messageID + "}", e);
        } catch (XMLSignatureException e) {
            throw new AISAdaptorException("Unexpected XMLSignatureException for message with ID {"
                    + messageID + "}", e);
        }
    }

    private Element getSignatureElement(String messageID, Document doc) {
        NodeList nl = doc.getElementsByTagName("Signature");
        if (nl.getLength() > 0) {
            return (Element) nl.item(0);
        } else {
            throw new AISAdaptorException("Signature element was not found for message with ID {"
                    + messageID + "}");
        }
    }

    private void removeSignatureElementIfAny(Document unsignedDoc) {
        NodeList nl = unsignedDoc.getElementsByTagName("Signature");
        if (nl.getLength() > 0) {
            Element sigElement = (Element) nl.item(0);
            unsignedDoc.getDocumentElement().removeChild(sigElement);
        }
    }

    private Document signDoc(Document doc) {
        try {
            Reference ref = buildSignatureReference();
            SignedInfo signedInfo = buildSignedInfo(ref);
            KeyInfo keyInfo = buildKeyInfo();
            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
            XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(dsc);
            return doc;
        } catch (MarshalException | XMLSignatureException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            throw new AISAdaptorException(e);
        }
    }

    private KeyInfo buildKeyInfo() {
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        KeyInfo keyInfo;
        List x509Content = new ArrayList();
        x509Content.add(certificate.getSubjectDN().getName());
        x509Content.add(certificate);
        X509Data xd = keyInfoFactory.newX509Data(x509Content);
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xd));
        return keyInfo;
    }

    private SignedInfo buildSignedInfo(Reference ref) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return signatureFactory.newSignedInfo(
                signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref)
        );
    }

    private Reference buildSignatureReference() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return signatureFactory.newReference("",
                signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null, null);
    }
}
