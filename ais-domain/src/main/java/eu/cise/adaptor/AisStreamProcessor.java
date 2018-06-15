package eu.cise.adaptor;

import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.adaptor.translate.CiseModelToCiseMessage;
import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;

import java.util.Optional;

/**
 * 
 */
public class AisStreamProcessor {

    private final AisMsgToCiseModel aisMsgToCiseModel;
    private final CiseModelToCiseMessage ciseModelToCiseMessage;
    private final AdaptorConfig config;
    private final AisNormalizer aisNormalizer;

    public AisStreamProcessor(AisNormalizer aisNormalizer,
                              AisMsgToCiseModel aisMsgToCiseModel,
                              CiseModelToCiseMessage ciseModelToCiseMessage,
                              AdaptorConfig config) {

        this.aisNormalizer = aisNormalizer;
        this.aisMsgToCiseModel = aisMsgToCiseModel;
        this.ciseModelToCiseMessage = ciseModelToCiseMessage;
        this.config = config;
    }

    public Flux<Message> process(Flux<String> aisStringFlux) {
        return toCiseMessageFlux(aisNormalizer.translate(aisStringFlux));
    }

    public Flux<Message> toCiseMessageFlux(Flux<AisMsg> aisMsgFlux) {
        return aisMsgFlux
                .map(aisMsgToCiseModel::translate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .buffer(config.getNumberOfEntitiesPerMessage())
                .map(ciseModelToCiseMessage::translate);
    }
}
