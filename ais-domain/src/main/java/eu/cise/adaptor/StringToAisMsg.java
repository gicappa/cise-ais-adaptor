package eu.cise.adaptor;

import eu.cise.adaptor.translate.Translator;
import reactor.core.publisher.Flux;

/**
 * This translator creates an abstraction on the AIS library used to read
 * the AIS streams.
 * <p>
 * This normalizer will take as an input a structure defined in a library that
 * represent an AIS message coming will normalize it to a {@link AisMsg}
 * that is a domain object.
 */
@FunctionalInterface
public interface StringToAisMsg extends Translator<Flux<String>, Flux<AisMsg>> {
}
