package eu.cise.adaptor;

import eu.cise.adaptor.context.DefaultAppContext;
import org.aeonbits.owner.ConfigFactory;

/**
 * The MainApp class is the application entry point. It accepts the
 * -d parameter to be more verbose when reporting errors.
 */
public class MainApp implements Runnable {

    private final ConfigPrinter configPrinter;
    private final Banner banner;
    private final AisApp aisApp;
    private final AppContext ctx;

    public MainApp(CertificateConfig config) {
        ctx = new DefaultAppContext(config);
        banner = new Banner();
        configPrinter = new ConfigPrinter(config);
        aisApp = new AisApp(ctx.makeSource(),
                            ctx.makeStreamProcessor(),
                            ctx.makeDispatcher(),
                            new AdaptorLogger.Slf4j(),
                            config);
    }

    /**
     * Application starts here
     *
     * @param args possible application parameters. The only one accepted is -d
     */
    public static void main(String[] args) {
        try {

            new MainApp(createConfig()).run();

        } catch (Throwable e) {
            System.err.println("An error occurred:\n\n" + e.getMessage() + "\n");

            if (optionDebug(args))
                e.printStackTrace();
        }
    }

    /**
     * Retrieve the configuration object.
     *
     * @return a CertificateConfig object.
     */
    private static CertificateConfig createConfig() {
        return ConfigFactory.create(CertificateConfig.class);
    }

    /**
     * Support extended '--debug' and brief '-d' format
     *
     * @param args the argument
     * @return true or false if the debug is enabled or not.
     */
    private static boolean optionDebug(String[] args) {
        return args.length > 0 && (args[0].equals("--debug") || args[0].equals("-d"));
    }

    /**
     * The when invoked, display a banner, dump the configuration and
     * starts the application.
     */
    @Override
    public void run() {
        banner.print();
        configPrinter.print();
        aisApp.run();
    }

}
