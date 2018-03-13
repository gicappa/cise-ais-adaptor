package eu.cise.adaptor;

import eu.cise.adaptor.tbs.AISMessageConsumer;
import eu.cise.adaptor.tbs.DefaultAISNormalizer;
import eu.cise.adaptor.tbs.FileAISSource;
import eu.cise.adaptor.tbs.SocketAISSource;
import jrc.cise.gw.sending.Dispatcher;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public interface AppContext {

    AISSource buildSource();

    class FileAppContext implements AppContext {

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
                            new DefaultAISNormalizer(),
                            new DefaultAISProcessor(new DefaultTranslator(config), dispatcher, config)));
        }
    }

    class SocketAppContext implements AppContext {

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
                            new DefaultAISNormalizer(),
                            new DefaultAISProcessor(new DefaultTranslator(config), dispatcher, config)));
        }
    }
}
