package eu.cise.adaptor.exceptions;

/**
 * Generic unchecked exception to manage error handling in the application
 */
@SuppressWarnings("unused")
public class AdaptorException extends RuntimeException {

    /**
     * Accepting the description of the exception occurred
     *
     * @param description what error occurred
     */
    public AdaptorException(String description) {
        super("\n\n" + description);
    }

    /**
     * Accepting the object causing the exception
     *
     * @param cause root cause of the exception
     */
    public AdaptorException(Throwable cause) {
        super(cause);
    }


    /**
     * Accepting the description and the object causing the exception
     *
     * @param description what error occurred
     * @param cause root cause of the exception
     */
    public AdaptorException(String description, Throwable cause) {
        super("\n\n" + description, cause);
    }

}
