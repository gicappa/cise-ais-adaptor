package eu.cise.adaptor.signature;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static eu.cise.adaptor.signature.ExceptionHandler.safe;

@SuppressWarnings("unused")
public class DefaultCertificateRegistry {

    private final KeyStoreInfo ksPrivate;
    private final KeyStoreInfo ksPublic;
    private final PrivateKeyInfo myPrivateKey;
    private Map<String, X509Certificate> publicCertMap = new ConcurrentHashMap<>();


    public DefaultCertificateRegistry(PrivateKeyInfo myPrivateKey,
                                      KeyStoreInfo ksPrivate,
                                      KeyStoreInfo ksPublic) {

        this.myPrivateKey = myPrivateKey;
        this.ksPrivate = ksPrivate;
        this.ksPublic = ksPublic;
    }

    public PrivateKey findPrivateKey() {
        return findPrivateKey(myPrivateKey.keyAlias());
    }

    public X509Certificate findPrivateCertificate() {
        return findPrivateCertificate(myPrivateKey.keyAlias());
    }

    public X509Certificate findPrivateCertificate(String keyAlias) {
        return safe(() -> (X509Certificate) ksPrivate.findCertificateChain(keyAlias)[0]);
    }

    public PrivateKey findPrivateKey(String keyAlias) {
        return safe(() -> ksPrivate.findPrivateKey(keyAlias, myPrivateKey.password()));
    }

    public X509Certificate findPublicCertificate(String certificateAlias) {
        return safe(() -> {
            if (!publicCertMap.containsKey(certificateAlias)) {
                publicCertMap.put(certificateAlias, ksPublic.findPublicCertificate(certificateAlias));
            }

            return publicCertMap.get(certificateAlias);

        });
    }

}
