package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.lib.signature.KeyStoreHelper;
import eu.cise.lib.signature.KeyStoreLoaderException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@SuppressWarnings("unused")
public class DefaultCertificateRegistry implements CertificateRegistry {


    private String privateJKSName;
    private String privateJKSPassword;
    private String privateKeyPassword;
    private String publicJKSName;
    private String publicJKSPassword;
    private String gatewayID;


    public DefaultCertificateRegistry(String gatewayID,
                                      String privateJKSName,
                                      String privateJKSPassword,
                                      String privateKeyPassword,
                                      String publicJKSName,
                                      String publicJKSPassword) {
        this.privateJKSName = privateJKSName;
        this.privateJKSPassword = privateJKSPassword;
        this.privateKeyPassword = privateKeyPassword;
        this.publicJKSName = publicJKSName;
        this.publicJKSPassword = publicJKSPassword;
        this.gatewayID = gatewayID;
    }

    @Override
    public Pair<Certificate[], PrivateKey> findPrivateKeyAndCertificateForCurrentGateway() {
        try {
            KeyStore keystore = KeyStoreHelper.getKeystore(this.getClass().getResourceAsStream("/" + privateJKSName), privateJKSPassword);
            KeyStoreHelper.PrivateKeyAndCertChain privateKeyAndCertChain = KeyStoreHelper.getPrivateKeyAndCertChain(keystore, privateKeyPassword, gatewayID + ".key");
            return new ImmutablePair<>(new Certificate[]{privateKeyAndCertChain.getCertificationChain()[0]}, privateKeyAndCertChain.getPrivateKey());
        } catch (KeyStoreLoaderException e) {
            throw new AISAdaptorException(e);
        }

    }

    @Override
    public X509Certificate findPublicCertificate(String certAliasInJKS) {
        try {
            KeyStore keystore = KeyStoreHelper.getKeystore(this.getClass().getResourceAsStream("/" + publicJKSName), publicJKSPassword);
            return (X509Certificate) keystore.getCertificate(certAliasInJKS);
        } catch (KeyStoreLoaderException | KeyStoreException e) {
            throw new AISAdaptorException(e);
        }
    }

}
