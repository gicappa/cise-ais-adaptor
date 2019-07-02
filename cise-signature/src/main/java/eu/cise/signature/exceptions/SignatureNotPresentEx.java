package eu.cise.signature.exceptions;

public class SignatureNotPresentEx extends SignatureEx {

    private static final String ERROR_MESSAGE
            = "Signature validation failed. No Signature element was found\n" +
            "in the message, so it' impossible to verify the signature.";

    public SignatureNotPresentEx() { super(ERROR_MESSAGE); }

}
