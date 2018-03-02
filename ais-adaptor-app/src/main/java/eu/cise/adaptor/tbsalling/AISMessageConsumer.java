package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.*;

import java.util.function.Consumer;

public class AISMessageConsumer implements Consumer<AISMessage> {

    private final AISNormalizer normalizer;
    private final AISProcessor processor;

    public AISMessageConsumer(DefaultAISNormalizer normalizer,
                              AISProcessor processor) {

        this.normalizer = normalizer;
        this.processor = processor;
    }

    @Override
    public void accept(AISMessage aisMessage) {
        processor.process(normalizer.normalize(aisMessage));
    }
}
