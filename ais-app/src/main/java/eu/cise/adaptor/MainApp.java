package eu.cise.adaptor;

import eu.cise.adaptor.normalize.AISNormalizer;
import jrc.cise.gw.sending.Dispatcher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";

    private final Banner banner;
    private final MainAISApp aisApp;

    private final ClassPathXmlApplicationContext appContext;
    private final Dispatcher dispatcher;
    private final AISNormalizer aisNormalizer;
    private final AISSource aisSource;

    public MainApp() {
        banner = new Banner();
        appContext = new ClassPathXmlApplicationContext("context-ais.xml");
        dispatcher = appContext.getBean("signingDispatcher", Dispatcher.class);
        aisNormalizer = appContext.getBean(AISNormalizer.class);
        aisSource = appContext.getBean(AISSource.class);

        aisApp = new MainAISApp(aisSource, aisNormalizer, dispatcher);
    }

    public static void main(String[] args) {
        try {
            new MainApp().run();

        } catch (Throwable e) {
            System.err.println("An error occurred:\n\n" + e.getMessage() + "\n");

            if (optionDebug(args))
                e.printStackTrace();
        }
    }

    private static boolean optionDebug(String[] args) {
        return args.length > 0 && (args[0].equals("--debug") || args[0].equals("-d"));
    }

    public void run() {
        banner.print(VERSION);

        aisApp.run();
    }

}
