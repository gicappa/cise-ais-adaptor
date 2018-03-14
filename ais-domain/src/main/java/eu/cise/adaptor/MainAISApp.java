package eu.cise.adaptor;

import jrc.cise.gw.sending.Dispatcher;
import org.aeonbits.owner.ConfigFactory;

public class MainAISApp<T> implements Runnable {

    private final AISAdaptorConfig config;
    private final AISSource aisSource;
    private final AISNormalizer<T> aisNormalizer;
    private final Dispatcher dispatcher;

    public MainAISApp(AISSource aisSource, AISNormalizer<T> aisNormalizer, Dispatcher dispatcher) {
        config = ConfigFactory.create(AISAdaptorConfig.class);
        this.aisSource = aisSource;
        this.aisNormalizer = aisNormalizer;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        AISMessageConsumer<T> consumer = createConsumer();

        aisSource.startConsuming(consumer);
    }

    private AISMessageConsumer<T> createConsumer() {
        return new AISMessageConsumer(aisNormalizer, createProcessor());
    }

    private DefaultAISProcessor createProcessor() {
        return new DefaultAISProcessor(createTranslator(), dispatcher, config);
    }

    private DefaultAISTranslator createTranslator() {
        return new DefaultAISTranslator(config);
    }
}
