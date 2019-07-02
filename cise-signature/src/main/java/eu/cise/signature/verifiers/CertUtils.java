package eu.cise.signature.verifiers;

import eu.cise.signature.exceptions.SignatureEx;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CertUtils {

    public X509Certificate stringToX509Cert(String base64Cert) {
        try {
            return readCertFromInputStream(stringToStream(addBeginEndString(removeCR(base64Cert))));
        } catch (CertificateException e) {
            throw new SignatureEx("");
        }
    }

    private String removeCR(String text) {
        return text.replace("\n", "");
    }

    private X509Certificate readCertFromInputStream(ByteArrayInputStream inStream)
    throws CertificateException {
        return (X509Certificate) newX509CertFactory().generateCertificate(inStream);
    }

    private ByteArrayInputStream stringToStream(String certText) {
        return new ByteArrayInputStream(certText.getBytes(UTF_8));
    }

    private CertificateFactory newX509CertFactory() throws CertificateException {
        return CertificateFactory.getInstance("X.509");
    }

    private String addBeginEndString(String certBase64) {
        return "-----BEGIN CERTIFICATE-----\n" + certBase64 + "\n-----END CERTIFICATE-----";
    }

}
