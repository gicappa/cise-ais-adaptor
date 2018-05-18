package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class DefaultCertificateRegistry implements CertificateRegistry {


    private String privateJKSName;
    private String keyStorePassword;
    private String privateKeyPassword;
    private String publicJKSName;
    private String publicJKSPassword;
    private String gatewayID;
    private Map<String, X509Certificate> publicCertMap = new ConcurrentHashMap<>();


    public DefaultCertificateRegistry(String gatewayID,
                                      String privateJKSName,
                                      String privateJKSPassword,
                                      String privateKeyPassword,
                                      String publicJKSName,
                                      String publicJKSPassword) {
        this.privateJKSName = privateJKSName;
        this.keyStorePassword = privateJKSPassword;
        this.privateKeyPassword = privateKeyPassword;
        this.publicJKSName = publicJKSName;
        this.publicJKSPassword = publicJKSPassword;
        this.gatewayID = gatewayID;
    }

    @Override
    public Pair<Certificate[], PrivateKey> findPrivateKeyAndCertificateForCurrentGateway() {
        String keyAliasForCurrentGateway = getKeyAliasForCurrentGateway();
        return findPrivateKeyAndCertificateForAlias(keyAliasForCurrentGateway);
    }

    @Override
    public Pair<Certificate[], PrivateKey> findPrivateKeyAndCertificateForAlias(String keyAlias) {
        try {
            KeyStore jks = getKeyStore(privateJKSName, keyStorePassword);
            Certificate[] certificateChain = jks.getCertificateChain(keyAlias);
            PrivateKey privateKey = (PrivateKey) jks.getKey(keyAlias, privateKeyPassword.toCharArray());
            return new ImmutablePair<>(new Certificate[]{certificateChain[0]}, privateKey);
        } catch (GeneralSecurityException | IOException e) {
            throw new AISAdaptorException(e);
        }
    }

    private KeyStore getKeyStore(String jksName, String keyStorePw) throws IOException, GeneralSecurityException {
        KeyStore jks = KeyStore.getInstance("JKS");
        jks.load(this.getClass().getResourceAsStream("/" + jksName), keyStorePw.toCharArray());
        return jks;
    }

    private String getKeyAliasForCurrentGateway() {
        return gatewayID + ".key";
    }

    @Override
    public X509Certificate findPublicCertificate(String certAliasInJKS) {
        try {
            if (publicCertMap.containsKey(certAliasInJKS)) {
                return publicCertMap.get(certAliasInJKS);
            } else {
                KeyStore keystore = getKeyStore(publicJKSName, publicJKSPassword);
                X509Certificate cert = (X509Certificate) keystore.getCertificate(certAliasInJKS);
                publicCertMap.put(certAliasInJKS, cert);
                return cert;
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new AISAdaptorException(e);
        }
    }

}
