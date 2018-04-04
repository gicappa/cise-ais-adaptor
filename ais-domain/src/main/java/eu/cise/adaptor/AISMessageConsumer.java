package eu.cise.adaptor;

import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.process.AISProcessor;

import java.util.Objects;
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

    @Override
    public Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
