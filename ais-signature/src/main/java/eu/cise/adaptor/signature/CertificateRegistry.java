package eu.cise.adaptor.signature;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface CertificateRegistry {

    PrivateKey findPrivateKey(String keyAlias, String password);
    X509Certificate findPrivateCertificate(String keyAlias);
    X509Certificate findPublicCertificate(String certificateAlias);
}
