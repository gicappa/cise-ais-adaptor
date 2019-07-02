package eu.cise.signature.exceptions;

public class MessageCertInvalidIssuerEx extends SignatureEx {

    private static final String ERROR_MESSAGE = "Signature verification failed.\n" +
            "The Issuer of the certificate contained in the message has been issued by a\n" +
            "signing-ca different from the one expected.";

    public MessageCertInvalidIssuerEx() {
        super(ERROR_MESSAGE);
    }

    public MessageCertInvalidIssuerEx(Throwable e) {
        super(ERROR_MESSAGE, e);
    }
}
