package eu.cise.adaptor.signature;

import eu.cise.servicemodel.v1.message.Message;
import org.apache.commons.lang3.tuple.Pair;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SignatureHelper {

    private static final CertificateRegistry certificateRegistry =
            new DefaultCertificateRegistry(
                    new PrivateKeyInfo("gw01", "cisecise"),
                    new KeyStoreInfo("cisePrivate.jks", "cisecise"),
                    new KeyStoreInfo("N/A", "N/A"));


    public Message sign(String signerCISEID, Message message) {
        Pair<Certificate[], PrivateKey> pair = certificateRegistry.findPrivateKeyAndCertificateForAlias(signerCISEID + ".key");
        SignatureDelegate sigDel = new SignatureDelegate((X509Certificate) (pair.getKey()[0]), pair.getValue());
        return sigDel.signMessageWithDelegatesPrivateKey(message);
    }
}
