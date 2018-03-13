package eu.cise.adaptor;

import eu.cise.adaptor.tbs.TBSAISNormalizer;
import eu.cise.adaptor.tbs.TBSSourceFactory;
import org.aeonbits.owner.ConfigFactory;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final AISSource aisSource;
    private final Banner banner;
    private final AISSourceFactory appContext;
    private final AISMessageConsumer consumer;
    private final AISAdaptorConfig config;

    public MainApp() {
        banner = new Banner();
        
        config = ConfigFactory.create(AISAdaptorConfig.class);
        consumer = new AISMessageConsumer(
                new TBSAISNormalizer(),
                new DefaultAISProcessor(new DefaultTranslator(config), dispatcher, config));


        appContext = new TBSSourceFactory(consumer);
        aisSource = appContext.newFileSource("/raw-ais/nmea-sample");
    }

    public static void main(String[] args) {
        new MainApp().run();
    }

    public void run() {
        try {
            banner.print(VERSION);
            aisSource.startConsuming();
        } catch (Throwable e) {
            throw new AISAdaptorException(e);
        }

    }

}
