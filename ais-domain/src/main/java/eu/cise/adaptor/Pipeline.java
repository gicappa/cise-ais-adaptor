package eu.cise.adaptor;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface Pipeline<A, B> {

    /**
     * The pipeline expects to process a series of objects of type A into
     * objects of type B.
     *
     * @param aFlux a flux of objects of type A
     * @return a flux of objects of type B
     */
    Flux<B> process(Flux<A> aFlux);
}
