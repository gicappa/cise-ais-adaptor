package eu.cise.adaptor;

@FunctionalInterface
public interface AISSource {

    <T> void startConsuming(AISMessageConsumer<T> consumer);

}
