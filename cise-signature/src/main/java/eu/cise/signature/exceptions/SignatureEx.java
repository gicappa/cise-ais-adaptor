package eu.cise.signature.exceptions;

public class SignatureEx extends RuntimeException {

    public SignatureEx(String message) { super(message); }

    public SignatureEx(Throwable ex) { super(ex); }

    public SignatureEx(String message, Throwable e) {
        super(message, e);
    }

}
