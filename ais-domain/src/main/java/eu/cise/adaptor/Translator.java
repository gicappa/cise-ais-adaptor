package eu.cise.adaptor;

import eu.cise.servicemodel.v1.message.Push;

import java.util.Optional;

@FunctionalInterface
public interface Translator {
    Optional<Push> translate(AISMsg aisMsg);
}
