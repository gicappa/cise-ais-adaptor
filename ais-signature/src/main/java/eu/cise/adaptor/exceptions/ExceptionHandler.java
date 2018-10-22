package eu.cise.adaptor.exceptions;

@SuppressWarnings("unused")
public class ExceptionHandler {

    public static <T> T safe(CertificateSupplier<T> supplier, String message) {
        try {
            return supplier.get();
        } catch (Exception e) {

            throw new AdaptorException(message, e);
        }
    }

    public static <T> T safe(CertificateSupplier<T> supplier) {
        return safe(supplier, "");
    }

    @FunctionalInterface
    public interface CertificateSupplier<T> {
        T get() throws Exception;
    }

}
