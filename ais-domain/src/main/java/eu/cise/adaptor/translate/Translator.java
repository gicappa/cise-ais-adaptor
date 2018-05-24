package eu.cise.adaptor.translate;

@FunctionalInterface
public interface Translator<A, B> {
    B translate(A type);
}
