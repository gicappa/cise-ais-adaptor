package eu.cise.signature.exceptions;

public class SigningCACertInvalidSignatureEx extends SignatureEx {

    private static final String ERROR_MESSAGE = "";

    public SigningCACertInvalidSignatureEx() {
        super(ERROR_MESSAGE);
    }

    public SigningCACertInvalidSignatureEx(Throwable e) {
        super(e);
    }
}
