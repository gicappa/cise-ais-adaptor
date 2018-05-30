package eu.cise.adaptor;

import org.aeonbits.owner.ConfigFactory;

/**
 * The MainApp class is the application entry point. It accepts the
 */
public class MainApp implements Runnable {

    private final Banner banner;
    private final AISApp aisApp;
    private final AppContext ctx;

    public MainApp(CertificateConfig config) {
        ctx = new DefaultAppContext(config);
        banner = new Banner();
        aisApp = new AISApp(ctx.makeSource(),
                ctx.makeStreamProcessor(),
                ctx.makeDispatcher(),
                config);
    }

    public static void main(String[] args) {
        try {

            new MainApp(createConfig()).run();

        } catch (Throwable e) {
            System.err.println("An error occurred:\n\n" + e.getMessage() + "\n");

            if (optionDebug(args))
                e.printStackTrace();
        }
    }

    private static CertificateConfig createConfig() {
        return ConfigFactory.create(CertificateConfig.class);
    }

    private static boolean optionDebug(String[] args) {
        return args.length > 0 && (args[0].equals("--debug") || args[0].equals("-d"));
    }

    @Override
    public void run() {
        banner.print();
        aisApp.run();
    }

}
