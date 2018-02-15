package eu.cise.adaptor;

/**
 * Generic unchecked exception to manage error handling in the application
 */
@SuppressWarnings("unused")
public class AISAdaptorException extends RuntimeException {
    public AISAdaptorException() {
        super();
    }

    public AISAdaptorException(String message) {
        super(message);
    }

    public AISAdaptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AISAdaptorException(Throwable cause) {
        super(cause);
    }

    protected AISAdaptorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
