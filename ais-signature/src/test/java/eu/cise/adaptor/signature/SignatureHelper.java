package eu.cise.adaptor.signature;

import eu.cise.servicemodel.v1.message.Message;
import org.apache.commons.lang3.tuple.Pair;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SignatureHelper {

    private static final DefaultCertificateRegistry certificateRegistry =
            new DefaultCertificateRegistry(
                    new PrivateKeyInfo("gw01", "cisecise"),
                    new KeyStoreInfo("cisePrivate.jks", "cisecise"),
                    new KeyStoreInfo("N/A", "N/A"));


    public Message sign(String signerCISEID, Message message) {
        X509Certificate certificate = certificateRegistry.findPrivateCertificate(keyAlias(signerCISEID));
        PrivateKey privateKey = certificateRegistry.findPrivateKey(keyAlias(signerCISEID));
        SignatureDelegate sigDel = new SignatureDelegate(certificate, privateKey);
        return sigDel.signMessageWithDelegatesPrivateKey(message);
    }

    private String keyAlias(String signerCISEID) {
        return signerCISEID + ".key";
    }
}
