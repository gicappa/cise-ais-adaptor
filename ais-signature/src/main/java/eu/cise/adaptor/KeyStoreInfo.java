package eu.cise.adaptor;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import static eu.cise.adaptor.exceptions.ExceptionHandler.safe;

public class KeyStoreInfo {

    private final String name;
    private final String password;
    private final KeyStore keyStore;

    public KeyStoreInfo(String name, String password) {
        this.name = name;
        this.password = password;
        this.keyStore = getKeyStore();
    }

    public PrivateKey findPrivateKey(String keyAlias, String password) {
        return safe(() -> (PrivateKey) getKeyStore().getKey(keyAlias, password.toCharArray()));
    }

    public Certificate[] findCertificateChain(String keyAlias) {
        return safe(() -> getKeyStore().getCertificateChain(keyAlias));
    }

    public X509Certificate findPublicCertificate(String certificateAlias) {
        return safe(() -> (X509Certificate) getKeyStore().getCertificate(certificateAlias));
    }

    public KeyStore getKeyStore() {
        return keyStore == null ? loadKeyStore(name, password) : keyStore;
    }

    private KeyStore loadKeyStore(String name, String password) {
        return safe(() -> {
            KeyStore jks = KeyStore.getInstance("JKS");
            jks.load(this.getClass().getResourceAsStream("/" + name), password.toCharArray());
            return jks;
        });
    }

}
