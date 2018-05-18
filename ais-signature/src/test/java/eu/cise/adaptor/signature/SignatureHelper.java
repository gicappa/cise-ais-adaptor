package eu.cise.adaptor.signature;

import eu.cise.servicemodel.v1.message.Message;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class SignatureHelper {

    private final DefaultCertificateRegistry registry;

    public SignatureHelper() {
        registry =
                new DefaultCertificateRegistry(
                        new PrivateKeyInfo("gw01", "cisecise"),
                        new KeyStoreInfo("cisePrivate.jks", "cisecise"),
                        new KeyStoreInfo("N/A", "N/A"));

    }

    public Message sign(PrivateKeyInfo myPrivateKey, Message message) {
        X509Certificate certificate = registry.findPrivateCertificate(myPrivateKey.keyAlias());
        PrivateKey privateKey = registry.findPrivateKey(myPrivateKey.keyAlias(), myPrivateKey.password());
        SignatureDelegate signature = new SignatureDelegate(certificate, privateKey);
        return signature.signMessageWithDelegatesPrivateKey(message);
    }

}
