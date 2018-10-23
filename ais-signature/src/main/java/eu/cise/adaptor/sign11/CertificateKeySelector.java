package eu.cise.adaptor.sign11;

import eu.cise.adaptor.exceptions.AdaptorException;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;

public class CertificateKeySelector extends KeySelector {
    @Override
    public KeySelectorResult select(KeyInfo keyInfo,
                                    Purpose purpose,
                                    AlgorithmMethod method,
                                    XMLCryptoContext context) {
        Optional<X509Certificate> cert = keyInfo
                .getContent()
                .stream()
                .flatMap(data -> ((X509Data) data).getContent().stream())
                .filter(o -> o instanceof X509Certificate)
                .findFirst();
        try {
            cert.get().checkValidity();
        } catch (CertificateException e) {
            throw new AdaptorException(e);
        }

        return cert.get()::getPublicKey;

    }
}
