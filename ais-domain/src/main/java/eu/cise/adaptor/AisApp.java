package eu.cise.adaptor;

import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Stream;

/**
 * Main class in the domain module. The run() method start the process of
 * reading from the selected AisSource, translate the ais info into cise
 * messages and dispatch the cise messages through a rest protocol to the
 * gateway.
 * <p>
 * Three phases are composing the transformation:
 * - toFluxString: generate the source and feed the flux stream of ais messages in
 * string format
 * - toCiseMessages: transform the string in {NMEAMessage}, then in {AISMessage}
 * and finally in {AisMsg}
 * - dispatchMessages: using multi threading dispatches the cise messages
 * to the gateway absorbing peaks
 */
public class AisApp implements Runnable {

    private final AdaptorConfig config;
    private final AisStreamGenerator aisStreamGenerator;
    private final AisStreamProcessor aisStreamProcessor;
    private final Dispatcher dispatcher;

    public AisApp(AisStreamGenerator aisStreamGenerator,
                  AisStreamProcessor aisStreamProcessor,
                  Dispatcher dispatcher,
                  AdaptorConfig config) {
        this.aisStreamGenerator = aisStreamGenerator;
        this.dispatcher = dispatcher;
        this.aisStreamProcessor = aisStreamProcessor;
        this.config = config;
    }

    @Override
    public void run() {
        dispatchMessages(toCiseMessages(toFluxString(openAisSource())));
    }

    /**
     * The publishOn allows to be flexible.
     *
     * @param messageStream is a stream of CISE messages
     */
    private void dispatchMessages(Flux<Message> messageStream) {
        messageStream
                .publishOn(Schedulers.elastic())
                .doOnNext(msg -> dispatcher.send(msg, config.getGatewayAddress()))
                .doOnError(e -> e.printStackTrace())
                .blockLast();
    }

    private Flux<Message> toCiseMessages(Flux<String> aisStringFlux) {
        return aisStreamProcessor.process(aisStringFlux);
    }

    private Stream<String> openAisSource() {
        return aisStreamGenerator.generate();
    }

    private Flux<String> toFluxString(Stream<String> source) {
        return Flux.fromStream(source);
    }

}