package eu.cise.adaptor;

import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Stream;

public class AISApp implements Runnable {

    private final AdaptorConfig config;
    private final AisSource aisSource;

    private final Dispatcher dispatcher;
    private final StreamProcessor streamProcessor;

    public AISApp(AisSource aisSource,
                  StreamProcessor streamProcessor,
                  Dispatcher dispatcher,
                  AdaptorConfig config) {
        this.aisSource = aisSource;
        this.dispatcher = dispatcher;
        this.streamProcessor = streamProcessor;
        this.config = config;
    }

    @Override
    public void run() {
        System.out.println("config = " + config); //TODO

        dispatchMessages(toCiseMessages(toFluxString(openAisSource())));
    }

    private Message dispatchMessages(Flux<Message> messageStream) {
        return messageStream
                .publishOn(Schedulers.elastic())
                .doOnNext(msg -> dispatcher.send(msg, config.getGatewayAddress()))
                .doOnError(System.err::println)
                .blockLast();
    }

    private Flux<Message> toCiseMessages(Flux<String> aisStringFlux) {
        return streamProcessor.process(aisStringFlux);
    }

    private Stream<String> openAisSource() {
        return aisSource.open();
    }

    private Flux<String> toFluxString(Stream<String> source) {
        return Flux.fromStream(source);
    }

}
