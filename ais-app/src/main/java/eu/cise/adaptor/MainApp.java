package eu.cise.adaptor;

import jrc.cise.gw.sending.Dispatcher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final Banner banner;
    private final MainAISApp aisApp;
    private final Dispatcher dispatcher;
    private final AISNormalizer aisNormalizer;
    private final AISSource aisSource;

    public MainApp() {
        banner = new Banner();
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("app-context.xml");
        dispatcher = appContext.getBean(Dispatcher.class);
        aisNormalizer = appContext.getBean(AISNormalizer.class);
        aisSource = appContext.getBean(AISSource.class);

        aisApp = new MainAISApp(aisSource, aisNormalizer, dispatcher);
    }

    public static void main(String[] args) {
        new MainApp().run();
    }

    public void run() {
        try {
            banner.print(VERSION);

            aisApp.run();

        } catch (Throwable e) {
            throw new AISAdaptorException(e);
        }

    }

}
