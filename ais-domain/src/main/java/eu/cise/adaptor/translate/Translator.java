package eu.cise.adaptor.translate;

import java.util.Optional;

@FunctionalInterface
public interface Translator<A, B> {
    Optional<B> translate(A type);
}
