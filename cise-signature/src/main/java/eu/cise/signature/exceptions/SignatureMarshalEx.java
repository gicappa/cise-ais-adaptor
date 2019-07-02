package eu.cise.signature.exceptions;

public class SignatureMarshalEx extends SignatureEx {

    private static final String ERROR_MESSAGE =
            "The signature verification failed while interpreting the XMLSignature element.\n" +
                    "Indicates an exceptional condition that occurred during the XML marshalling\n" +
                    "or unmarshalling process.";

    public SignatureMarshalEx() {
        super(ERROR_MESSAGE);
    }

    public SignatureMarshalEx(Throwable e) {
        super(ERROR_MESSAGE, e);
    }

}
