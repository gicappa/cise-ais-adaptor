package eu.cise.signature;

import eu.cise.signature.certificates.DefaultCertificateRegistry;
import eu.cise.signature.certificates.KeyStoreInfo;
import eu.cise.signature.certificates.PrivateKeyInfo;
import eu.cise.signature.exceptions.SignatureEx;
import eu.cise.signature.signers.DefaultDomSigner;
import eu.cise.signature.verifiers.DefaultDomVerifier;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

@SuppressWarnings("unused")
public class SignatureServiceBuilder {

    private static final String KS_NAME_MISSING =
            "To build a SignatureService object it's mandatory to specify a keystore name" +
                    "using the .withKeyStoreName(...) method";
    private static final String KS_PASS_MISSING =
            "To build a SignatureService object it's mandatory to specify a keystore password" +
                    "using the .withKeyStorePassword(...) method";
    private static final String PK_ALIAS_MISSING =
            "To build a SignatureService object it's mandatory to specify a private key alias" +
                    "using the .withPrivateKeyAlias(...) method";
    private static final String PK_PASS_MISSING =
            "To build a SignatureService object it's mandatory to specify a private key password" +
                    "using the .withPrivateKeyPassword(...) method";

    private final XmlMapper xmlMapper;
    private CertificateRegistry registry;
    private String privateKeyAlias;
    private String keyStoreName;
    private String keyStorePassword;
    private String privateKeyPassword;

    // building the class
    private SignatureServiceBuilder(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    public static SignatureServiceBuilder newSignatureService() {
        return newSignatureService(new DefaultXmlMapper());
    }

    public static SignatureServiceBuilder newSignatureService(XmlMapper xmlMapper) {
        return new SignatureServiceBuilder(xmlMapper);
    }

    // adding information to the builder
    public SignatureServiceBuilder withPrivateKeyAlias(String alias) {
        this.privateKeyAlias = alias;
        return this;
    }

    public SignatureServiceBuilder withPrivateKeyPassword(String password) {
        this.privateKeyPassword = password;
        return this;
    }

    public SignatureServiceBuilder withKeyStoreName(String keyStoreName) {
        this.keyStoreName = keyStoreName;
        return this;
    }

    public SignatureServiceBuilder withKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public SignatureService build() {
        notNullOrEmpty(keyStoreName, KS_NAME_MISSING);
        notNullOrEmpty(keyStorePassword, KS_PASS_MISSING);
        notNullOrEmpty(privateKeyAlias, PK_ALIAS_MISSING);
        notNullOrEmpty(privateKeyPassword, PK_PASS_MISSING);

        KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keyStoreName, keyStorePassword);
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(privateKeyAlias, privateKeyPassword);

        registry = new DefaultCertificateRegistry(keyStoreInfo);

        DomVerifier verifier = new DefaultDomVerifier(registry);
        DomSigner signer = new DefaultDomSigner(registry, privateKeyInfo);

        return new DefaultSignatureService(signer, verifier, xmlMapper);
    }

    private void notNullOrEmpty(String str, String message) {
        if (str == null || str.isEmpty()) throw new SignatureEx(message);
    }
}
