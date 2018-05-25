package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.process.DefaultAISProcessor;
import eu.cise.adaptor.tbs.NMEAMessageTranslator;
import eu.cise.adaptor.translate.ModelTranslator;
import eu.cise.adaptor.translate.ServiceTranslator;
import org.aeonbits.owner.ConfigFactory;

public class AISApp<T> implements Runnable {

    private final AISAdaptorConfig config;
    private final AISSource aisSource;
    private final AISNormalizer<T> aisNormalizer;
    private final Dispatcher dispatcher;
    private final NMEAMessageTranslator nmeaTranslator;

    public AISApp(AISSource aisSource, AISNormalizer aisNormalizer, Dispatcher dispatcher) {
        config = ConfigFactory.create(AISAdaptorConfig.class);
        this.aisSource = aisSource;
        this.aisNormalizer = aisNormalizer;
        this.dispatcher = dispatcher;
        this.nmeaTranslator = new NMEAMessageTranslator();
        System.out.println("config = " + config); //TODO
    }

    @Override
    public void run() {


        aisSource.open()
                .map(nmeaTranslator::translate)
                .map(aisNormalizer::normalize)
        ;


    }

    private AISMessageConsumer<T> createConsumer() {
        return new AISMessageConsumer(aisNormalizer, createProcessor());
    }

    private DefaultAISProcessor createProcessor() {
        return new DefaultAISProcessor(new ModelTranslator(config), new ServiceTranslator(config), dispatcher, config);
    }

}
