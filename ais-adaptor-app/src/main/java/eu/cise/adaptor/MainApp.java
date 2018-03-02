package eu.cise.adaptor;

import eu.cise.adaptor.tbsalling.AISMessageConsumer;
import eu.cise.adaptor.tbsalling.DefaultAISNormalizer;
import eu.cise.adaptor.tbsalling.FileAISSource;
import jrc.cise.gw.sending.Dispatcher;
import org.aeonbits.owner.ConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final AISSource aisConsumer;
    private final Banner banner;
    private final ApplicationContext context;
    private final Dispatcher dispatcher;

    public MainApp() {
        this.banner = new Banner();
        this.context = new ClassPathXmlApplicationContext("/context-ais.xml");

//        this.aisConsumer = context.getBean("socketAISSource", AISSource.class);

        AISAdaptorConfig config = ConfigFactory.create(AISAdaptorConfig.class);

        this.dispatcher = context.getBean("signingDispatcher", Dispatcher.class);

        this.aisConsumer = new FileAISSource(
                "/raw-ais/nmea-sample",
                new AISMessageConsumer(
                        new DefaultAISNormalizer(),
                        new DefaultAISProcessor(new DefaultTranslator(config), dispatcher, config)));
    }

    public static void main(String[] args) {
        new MainApp().run();
    }

    public void run() {
        try {
            banner.print(VERSION);

            System.out.println("Press a key to start consuming AIS Messages.");
            System.in.read();

            aisConsumer.run();
        } catch (Throwable e) {
            throw new AISAdaptorException(e);
        }

    }

}
