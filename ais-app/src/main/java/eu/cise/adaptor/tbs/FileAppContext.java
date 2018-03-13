package eu.cise.adaptor.tbs;

import eu.cise.adaptor.*;
import jrc.cise.gw.sending.Dispatcher;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FileAppContext implements AppContext {

    private final ClassPathXmlApplicationContext context;
    private final AISAdaptorConfig config;
    private final Dispatcher dispatcher;

    public FileAppContext() {
        context = new ClassPathXmlApplicationContext("/context-ais.xml");
        config = ConfigFactory.create(AISAdaptorConfig.class);
        dispatcher = context.getBean("signingDispatcher", Dispatcher.class);
    }

    @Override
    public AISSource buildSource() {
        return new FileAISSource(
                "/raw-ais/nmea-sample",
                new AISMessageConsumer(
                        new TBSAISNormalizer(),
                        new DefaultAISProcessor(new DefaultTranslator(config), dispatcher, config)));
    }
}
