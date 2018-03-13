package eu.cise.adaptor.tbs;


import eu.cise.adaptor.*;
import jrc.cise.gw.sending.Dispatcher;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SocketAppContext implements AppContext {

    private final ClassPathXmlApplicationContext context;
    private final AISAdaptorConfig config;
    private final Dispatcher dispatcher;

    public SocketAppContext() {
        context = new ClassPathXmlApplicationContext("/context-ais.xml");
        config = ConfigFactory.create(AISAdaptorConfig.class);
        dispatcher = context.getBean("signingDispatcher", Dispatcher.class);
    }

    @Override
    public AISSource buildSource() {
        return new SocketAISSource(
                "localhost",
                8080,
                new AISMessageConsumer(
                        new TBSAISNormalizer(),
                        new DefaultAISProcessor(new DefaultTranslator(config), dispatcher, config)));
    }
}