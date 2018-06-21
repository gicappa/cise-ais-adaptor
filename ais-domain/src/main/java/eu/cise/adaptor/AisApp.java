package eu.cise.adaptor;

import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

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
    private final TranslatorsPipeline aisStreamProcessor;
    private final Dispatcher dispatcher;

    /**
     * The App is mainly built with a stream generator a processor and a message
     * dispatcher.
     * <p>
     * The generator will produce a stream of strings reading them from
     * different possible sources:
     *   - plain text files
     *   - tcp sockets
     *   - whatever other AIS information producer
     * <p>
     * The processor will transform the incoming stream of ais strings into a
     * sequence of CISE push messages objects. The transformation will be
     * performed in multiple stages.
     *   - String -> AisMsg: where the latter is a decoded representation of the
     *   message in an domain object
     *   - AisMsg -> Optional<Entity>: the ais message is translated into a cise
     *   vessel if is of type 1,2,3 or 5, otherwise it will be an empty optional.
     *   - List<Entity> -> Push:
     *
     * @param aisStreamGenerator stream generator of ais strings
     * @param aisStreamProcessor stream processor of ais strings into cise messages
     * @param dispatcher dispatcher of cise messages
     * @param config application configuration object
     */
    public AisApp(AisStreamGenerator aisStreamGenerator,
                  TranslatorsPipeline aisStreamProcessor,
                  Dispatcher dispatcher,
                  AdaptorConfig config) {
        this.aisStreamGenerator = aisStreamGenerator;
        this.aisStreamProcessor = aisStreamProcessor;
        this.dispatcher = dispatcher;
        this.config = config;
    }

    /**
     * The run method is the domain object composing the behaviour.
     * Generate AIS string messages and pipeline them into the transformation
     * into cise messages and that dispatches them.
     */
    @Override
    public void run() {
        dispatchMessages(aisStreamProcessor.process(Flux.fromStream(aisStreamGenerator.generate())));
    }

    /**
     * The flux stream allows to publish in a thread pool the dispatching
     * so to make http request in a parallel non blocking manner.
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

}