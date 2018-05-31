package eu.cise.adaptor;

import eu.cise.adaptor.context.DefaultAppContext;
import org.aeonbits.owner.ConfigFactory;

import static java.util.stream.Collectors.toMap;

/**
 * The MainApp class is the application entry point. It accepts the
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
                config);
    }

    public static void main(String[] args) {
        try {

            CertificateConfig config = createConfig();

            new MainApp(config).run();

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
        configPrinter.print();
        aisApp.run();
    }

}
