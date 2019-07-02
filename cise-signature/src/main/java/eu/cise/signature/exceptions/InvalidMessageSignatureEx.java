package eu.cise.signature.exceptions;

public class InvalidMessageSignatureEx extends SignatureEx {

    private static final String ERROR_MESSAGE =
            "The signature verification failed. The CISE message signature has been verified\n" +
                    "against the adaptor certificate and the public key contained in the " +
                    "certificate could not verify the signature.\n";

    public InvalidMessageSignatureEx(String message) {
        super(message + "\n" + ERROR_MESSAGE);
    }

    public InvalidMessageSignatureEx() {
        super(ERROR_MESSAGE);
    }

    public InvalidMessageSignatureEx(Throwable e) {
        super(ERROR_MESSAGE, e);
    }
}
