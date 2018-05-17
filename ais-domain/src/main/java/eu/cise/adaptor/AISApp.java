package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.process.DefaultAISProcessor;
import eu.cise.adaptor.translate.DefaultAISTranslator;
import org.aeonbits.owner.ConfigFactory;

public class AISApp<T> implements Runnable {

    private final AISAdaptorConfig config;
    private final AISSource aisSource;
    private final AISNormalizer<T> aisNormalizer;
    private final Dispatcher dispatcher;

    public AISApp(AISSource aisSource, AISNormalizer<T> aisNormalizer, Dispatcher dispatcher) {
        config = ConfigFactory.create(AISAdaptorConfig.class);
        this.aisSource = aisSource;
        this.aisNormalizer = aisNormalizer;
        this.dispatcher = dispatcher;

        System.out.println("config = " + config);
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
