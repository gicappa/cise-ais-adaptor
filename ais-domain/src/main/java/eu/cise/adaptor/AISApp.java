package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.process.AISProcessor;
import eu.cise.adaptor.process.DefaultAISProcessor;
import eu.cise.adaptor.translate.ModelTranslator;
import eu.cise.adaptor.translate.ServiceTranslator;
import org.aeonbits.owner.ConfigFactory;

public class AISApp implements Runnable {

    private final AISAdaptorConfig config;
    private final AISSource aisSource;
    private final AISNormalizer aisNormalizer;
    private final Dispatcher dispatcher;

    public AISApp(AISSource aisSource, AISNormalizer aisNormalizer, Dispatcher dispatcher) {
        this.config = ConfigFactory.create(AISAdaptorConfig.class);
        this.aisSource = aisSource;
        this.aisNormalizer = aisNormalizer;
        this.dispatcher = dispatcher;
        System.out.println("config = " + config); //TODO
    }

    @Override
    public void run() {
        AISProcessor p = createProcessor();

        aisSource.open()
                .map(aisNormalizer::translate)
                .map(aisMsg -> aisMsg.map(x -> p.process(x)))
                .filter(d -> d.map(a -> a.isOK()).orElse(true))
                .forEach(a -> System.out.println("a = " + a));


    }

    private DefaultAISProcessor createProcessor() {
        return new DefaultAISProcessor(new ModelTranslator(config), new ServiceTranslator(config), dispatcher, config);
    }

}
