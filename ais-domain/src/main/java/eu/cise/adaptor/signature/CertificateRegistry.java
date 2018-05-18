package eu.cise.adaptor.signature;

import org.apache.commons.lang3.tuple.Pair;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public interface CertificateRegistry {

   Pair<Certificate[], PrivateKey> findPrivateKeyAndCertificateForCurrentGateway();

   X509Certificate findPublicCertificate(String certIdentifierInJKS);

}
