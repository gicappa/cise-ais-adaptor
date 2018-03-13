package eu.cise.adaptor;

import java.util.function.Consumer;

public class AISMessageConsumer<T> implements Consumer<T> {

    private final AISNormalizer normalizer;
    private final AISProcessor processor;

    public AISMessageConsumer(AISNormalizer<T> normalizer, AISProcessor processor) {
        this.normalizer = normalizer;
        this.processor = processor;
    }

    @Override
    public void accept(T aisMessage) {
        processor.process(normalizer.normalize(aisMessage));
    }
}
