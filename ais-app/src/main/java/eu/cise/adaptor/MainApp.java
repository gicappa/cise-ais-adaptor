package eu.cise.adaptor;

import eu.cise.adaptor.tbs.FileAppContext;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final AISSource aisSource;
    private final Banner banner;
    private final AppContext appContext;

    public MainApp() {
        banner = new Banner();
        appContext = new FileAppContext();
        aisSource = appContext.buildSource();
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
