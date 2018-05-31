package eu.cise.adaptor.exceptions;

@SuppressWarnings("unused")
public class ExceptionHandler {
    public static <T> T safe(CertificateSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new AdaptorException(e);
        }
    }

    @FunctionalInterface
    public interface CertificateSupplier<T> {
        T get() throws Exception;
    }

}
