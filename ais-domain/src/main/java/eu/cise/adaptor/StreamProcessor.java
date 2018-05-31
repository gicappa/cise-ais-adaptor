package eu.cise.adaptor;

import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.adaptor.translate.CiseModelToCiseMessage;
import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;

import java.util.Optional;

public class StreamProcessor {

    private final AisMsgToCiseModel aisMsgToCiseModel;
    private final CiseModelToCiseMessage ciseModelToCiseMessage;
    private final AISNormalizer aisNormalizer;

    public StreamProcessor(AISNormalizer aisNormalizer,
                           AisMsgToCiseModel aisMsgToCiseModel,
                           CiseModelToCiseMessage ciseModelToCiseMessage) {

        this.aisNormalizer = aisNormalizer;
        this.aisMsgToCiseModel = aisMsgToCiseModel;
        this.ciseModelToCiseMessage = ciseModelToCiseMessage;
    }

    public Flux<Message> process(Flux<String> aisStringFlux) {
        return toCiseMessageFlux(aisNormalizer.translate(aisStringFlux));
    }

    public Flux<Message> toCiseMessageFlux(Flux<AISMsg> aisMsgFlux) {
        return aisMsgFlux
                .map(aisMsgToCiseModel::translate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .buffer(1)
                .map(ciseModelToCiseMessage::translate);
    }
}
