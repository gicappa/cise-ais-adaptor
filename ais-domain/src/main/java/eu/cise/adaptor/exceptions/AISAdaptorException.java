package eu.cise.adaptor.exceptions;

/**
 * Generic unchecked exception to manage error handling in the application
 */
@SuppressWarnings("unused")
public class AISAdaptorException extends RuntimeException {
    public AISAdaptorException() {
        super();
    }

    public AISAdaptorException(String message) {
        super("\n\n" + message);
    }

    public AISAdaptorException(String message, Throwable cause) {
        super("\n\n" +message, cause);
    }

    public AISAdaptorException(Throwable cause) {
        super(cause);
    }

}
