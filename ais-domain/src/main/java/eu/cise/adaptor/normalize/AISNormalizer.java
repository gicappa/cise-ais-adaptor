package eu.cise.adaptor.normalize;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.translate.Translator;
import reactor.core.publisher.Flux;

/**
 * What a normalizer does is to abstract from the AIS library used to read
 * the AIS streams.
 * <p>
 * The normalizer will take as an input a structure defined in a library that
 * represent an AIS message coming will normalize it to a {@link AISMsg}
 * that is a domain object.
 */
@FunctionalInterface
public interface AISNormalizer extends Translator<Flux<String>, Flux<AISMsg>> {
}
