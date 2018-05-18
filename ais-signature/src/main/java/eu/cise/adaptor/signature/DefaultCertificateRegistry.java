package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static eu.cise.adaptor.signature.ExceptionHandler.safe;

@SuppressWarnings("unused")
public class DefaultCertificateRegistry implements CertificateRegistry {

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

    @Override
    public Pair<Certificate[], PrivateKey> findPrivateKeyAndCertificateForCurrentGateway() {
        return findPrivateKeyAndCertificateForAlias(myPrivateKey.keyAlias());
    }

    @Override
    public Pair<Certificate[], PrivateKey> findPrivateKeyAndCertificateForAlias(String keyAlias) {
        return new ImmutablePair<>(findCertificate(keyAlias), findPrivateKey(keyAlias));
    }

    public Certificate[] findCertificate(String keyAlias) {
        return safe(() -> ksPrivate.findCertificateChain(keyAlias));
    }

    public PrivateKey findPrivateKey(String keyAlias) {
        return safe(() -> ksPrivate.findPrivateKey(keyAlias, myPrivateKey.password()));
    }

    @Override
    public X509Certificate findPublicCertificate(String certificateAlias) {
        return safe(() -> {
            if (!publicCertMap.containsKey(certificateAlias)) {
                publicCertMap.put(certificateAlias, ksPublic.findPublicCertificate(certificateAlias));
            }

            return publicCertMap.get(certificateAlias);

        });
    }

}
