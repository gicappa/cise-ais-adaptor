package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;

@SuppressWarnings("unused")
public class ExceptionHandler {
    public static <T> T safe(CertificateSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new AISAdaptorException(e);
        }
    }

    @FunctionalInterface
    public interface CertificateSupplier<T> {
        T get() throws Exception;
    }

}
