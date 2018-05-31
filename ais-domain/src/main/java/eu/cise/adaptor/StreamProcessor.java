package eu.cise.adaptor;

import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.adaptor.translate.CiseModelToCiseMessage;
import eu.cise.datamodel.v1.entity.Entity;
import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;

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
                .<Entity>handle((optEntity, sink) -> {
                    if (!optEntity.isPresent()) return;

                    sink.next(optEntity.get());
                    sink.complete();
                })
                .buffer(1)
                .map(ciseModelToCiseMessage::translate);
    }
}
