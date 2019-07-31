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

import eu.cise.signature.DomVerifier;
import eu.cise.signature.exceptions.*;
import eu.cise.signature.exceptions.SignatureEx;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This is the original DomVerifier provided by the RTI. It seems to be always succeeding without
 * checking the certificate chain.
 */
@SuppressWarnings("unused")
public class RtiDomVerifier implements DomVerifier {

    private static final String SCHEMA = "http://www.w3.org/2001/XMLSchema-instance";

    private static boolean algEquals(String algURI, String algName) {
        if (algName.equalsIgnoreCase("DSA")
                && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
            return true;
        } else return algName.equalsIgnoreCase("RSA")
                && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1);
    }

    @Override
    public void verify(Document document) {
        verify(new KeyValueInCertificateKeySelector(), document);
    }

    private void verify(KeySelector keySelector, Document doc) {
        try {
            // Find Signature element
            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                nl = doc.getElementsByTagNameNS(SCHEMA, "Signature");
                if (nl.getLength() == 0) {
                    throw new SignatureNotPresentEx();
                }
            }

            // Create a DOM XMLSignatureFactory that will be used to unmarshal the
            // document containing the XMLSignature
            XMLSignatureFactory factory = newSignatureFactory();

            for (int i = 0; i < nl.getLength(); i++) {

                // Create a DOMValidateContext and specify a KeyValue KeySelector
                // and document context
                DOMValidateContext valContext = new DOMValidateContext(keySelector, nl.item(i));

                // unmarshal the XMLSignature
                XMLSignature signature = factory.unmarshalXMLSignature(valContext);

                // Validate the XMLSignature (generated above)
                // Check core validation status
                if (!signature.validate(valContext)) {
                    throw new InvalidMessageSignatureEx(handleVerificationErrorMsg(signature,
                                                                                   valContext));

                }
            }
        } catch (XMLSignatureException xe) {
            throw new InvalidMessageSignatureEx(xe.getCause() != null ? xe.getCause() : xe);
        } catch (MarshalException e) {
            throw new SignatureMarshalEx();
        }
    }

    /**
     * TODO the code here is just checking one level because in the for loop it always
     * TODO does not switch from one certificate to the other
     *
     * @param x509certificateToVerify        certificate to be verified
     * @param collectionX509CertificateChain list of certificates
     */
    private void verifyCertificateChain(X509Certificate x509certificateToVerify,
                                        Certificate[] collectionX509CertificateChain) {

        if (collectionX509CertificateChain == null || collectionX509CertificateChain.length == 0) {
            // no root certificates to use in validation
            return;
        }

        // Working down the chain, for every certificate in the chain,
        // verify that the subject of the certificate is the issuer of the
        // next certificate in the chain.
        for (Certificate cert : collectionX509CertificateChain) {

            if (cert == null) {
                return;
            }

            if (!(cert instanceof X509Certificate)) {
                throw new SigningCACertInvalidSignatureEx();
            }

            X509Certificate x509certificate = (X509Certificate) cert;

            // Principal principalIssuer = x509certificate.getIssuerDN();
            // Principal principalSubject = x509certificate.getSubjectDN();
            PublicKey publickey = x509certificate.getPublicKey();
            try {
                // verify if the certificate is correct against the public key
                x509certificateToVerify.verify(publickey);
            } catch (Exception e) {
                throw new SigningCACertInvalidSignatureEx(e);
            }

            try {
                // verifies if the certificate is valid against the current date
                x509certificateToVerify.checkValidity();
            } catch (GeneralSecurityException e) {
                throw new SigningCACertInvalidDateEx(e);
            }

            // Issuers are equal ?
            if (!x509certificate.getSubjectDN().equals(x509certificateToVerify.getIssuerDN())) {
                throw new MessageCertInvalidIssuerEx();
            }
        }
    }

    private String handleVerificationErrorMsg(XMLSignature signature,
                                              DOMValidateContext valContext) throws XMLSignatureException {

        StringBuilder error = new StringBuilder();
        error.append("Signature failed core validation.").append(System.lineSeparator());
        boolean sv = signature.getSignatureValue().validate(valContext);
        error.append("Signature validation status: ").append(sv).append(System.lineSeparator());
        // check the validation status of each Reference
        Iterator i = signature.getSignedInfo().getReferences().iterator();
        for (int j = 0; i.hasNext(); j++) {

            boolean refValid
                    = ((Reference) i.next()).validate(valContext);
            error.append("ref[").append(j).append("] validity status: ").append(refValid).append(System.lineSeparator());
        }

        return error.toString();
    }

    private XMLSignatureFactory newSignatureFactory() {
        try {
            return XMLSignatureFactory.getInstance("DOM", "XMLDSig");
        } catch (NoSuchProviderException e) {
            throw new SignatureEx("No such security provider", e);
        }
    }

    /**
     *
     */
    private static class SimpleKeySelectorResult implements KeySelectorResult {

        private final PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        @Override
        public Key getKey() {
            return pk;
        }
    }

    /**
     * KeySelector which retrieves the public key out of the KeyValue element
     * and returns it. NOTE: If the key algorithm doesn't match signature
     * algorithm, then the public key will be ignored.
     */
    private class KeyValueInCertificateKeySelector extends KeySelector {

        private final Certificate[] chainCertificates;

        KeyValueInCertificateKeySelector() {
            chainCertificates = null;
        }

        @Override
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose, AlgorithmMethod method,
                                        XMLCryptoContext context) throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }

            SignatureMethod sm = (SignatureMethod) method;

            List list = keyInfo.getContent();

            for (Object value : list) {
                X509Data x509Data = (X509Data) value;

                for (Object o : x509Data.getContent()) {
                    if (o instanceof X509Certificate) {
                        X509Certificate x509Certificate = (X509Certificate) o;

                        try {
                            x509Certificate.checkValidity();
                        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                            Logger.getLogger(getClass().getName())
                                    .log(Level.WARNING, null, "Invalid certificate " +
                                            "validity. " + e.getMessage());
                            throw new KeySelectorException("Invalid certificate validity. " + e.getMessage());
                        }

                        // verify chain
                        try {
                            verifyCertificateChain(x509Certificate, chainCertificates);
                        } catch (Exception e) {
                            throw new KeySelectorException("Failed to verify Certificate Chain. " + e.getMessage());
                        }

                        PublicKey pk = x509Certificate.getPublicKey();

                        // make sure algorithm is compatible with method
                        if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                            return new SimpleKeySelectorResult(pk);
                        }
                    }
                }

            }
            throw new KeySelectorException("No KeyValue element found!");
        }

    }

    private class KeyValueKeySelector extends KeySelector {

        private final PublicKey publicKey;

        private final Certificate[] chainCertificates;

        KeyValueKeySelector(PublicKey pubKey) {
            publicKey = pubKey;
            chainCertificates = null;
        }

        KeyValueKeySelector(PublicKey pubKey, Certificate[] certificateChain) {
            publicKey = pubKey;
            chainCertificates = certificateChain;
        }

        @SuppressWarnings("all")
        @Override
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose, AlgorithmMethod method,
                                        XMLCryptoContext context) throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            List list = keyInfo.getContent();

            for (int i = 0; i < list.size(); i++) {
                Object object = list.get(i);

                if (object instanceof X509Data) {
                    X509Data x509Data = (X509Data) object;
                    Iterator xi = x509Data.getContent().iterator();
                    while (xi.hasNext()) {
                        Object o = xi.next();
                        if (o instanceof X509Certificate) {
                            X509Certificate x509Certificate = (X509Certificate) o;

                            try {
                                verifyCertificateChain(x509Certificate, chainCertificates);
                            } catch (Exception e) {
                                throw new KeySelectorException("Failed to verify Certificate " +
                                                                       "Chain. " + e.getMessage());
                            }

                            PublicKey pk = x509Certificate.getPublicKey();

                            // make sure algorithm is compatible with method
                            if (algEquals(sm.getAlgorithm(), (publicKey != null ?
                                                              publicKey.getAlgorithm() :
                                                              pk.getAlgorithm()))) {
                                return new SimpleKeySelectorResult(publicKey != null ? publicKey
                                                                                     : pk);
                            }
                        }
                    }
                } else {
                    if (object instanceof KeyValue) {
                        PublicKey pk;
                        try {
                            pk = ((KeyValue) object).getPublicKey();
                        } catch (KeyException ke) {
                            throw new KeySelectorException(ke);
                        }
                        // make sure algorithm is compatible with method
                        if (algEquals(sm.getAlgorithm(), (publicKey != null ?
                                                          publicKey.getAlgorithm() :
                                                          pk.getAlgorithm()))) {
                            return new SimpleKeySelectorResult(publicKey != null ? publicKey : pk);
                        }
                    }
                }

            }
            throw new KeySelectorException("No KeyValue element found!");
        }
    }
}
