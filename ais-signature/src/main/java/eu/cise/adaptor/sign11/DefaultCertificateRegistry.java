package eu.cise.adaptor.sign11;

import eu.cise.adaptor.CertificateRegistry;
import eu.cise.adaptor.KeyStoreInfo;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static eu.cise.adaptor.exceptions.ExceptionHandler.safe;

@SuppressWarnings("unused")
public class DefaultCertificateRegistry implements CertificateRegistry {

    private final KeyStoreInfo ksPrivate;
    private final KeyStoreInfo ksPublic;
    private Map<String, X509Certificate> publicCertMap = new ConcurrentHashMap<>();


    public DefaultCertificateRegistry(KeyStoreInfo ksPrivate, KeyStoreInfo ksPublic) {
        this.ksPrivate = ksPrivate;
        this.ksPublic = ksPublic;
    }

    @Override
    public PrivateKey findPrivateKey(String keyAlias, String password) {
        return safe(() -> ksPrivate.findPrivateKey(keyAlias, password));
    }

    @Override
    public X509Certificate findPrivateCertificate(String keyAlias) {
        return safe(() -> (X509Certificate) ksPrivate.findCertificateChain(keyAlias)[0],
                    "The keyAlias [" + keyAlias + "] was not found in the keystore.");
    }

    @Override
    public X509Certificate findPublicCertificate(String certificateAlias) {
        return safe(() -> {
            if (!publicCertMap.containsKey(certificateAlias)) {
                publicCertMap.put(certificateAlias,
                                  ksPublic.findPublicCertificate(certificateAlias));
            }

            return publicCertMap.get(certificateAlias);

        });
    }

}
