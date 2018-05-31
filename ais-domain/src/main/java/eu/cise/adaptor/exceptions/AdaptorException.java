package eu.cise.adaptor.exceptions;

/**
 * Generic unchecked exception to manage error handling in the application
 */
@SuppressWarnings("unused")
public class AdaptorException extends RuntimeException {
    public AdaptorException() {
        super();
    }

    public AdaptorException(String message) {
        super("\n\n" + message);
    }

    public AdaptorException(String message, Throwable cause) {
        super("\n\n" + message, cause);
    }

    public AdaptorException(Throwable cause) {
        super(cause);
    }

}
