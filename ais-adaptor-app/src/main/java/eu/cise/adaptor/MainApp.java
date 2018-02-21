package eu.cise.adaptor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final AISConsumer aisConsumer;
    private final Banner banner;
    private final ApplicationContext context;

    public MainApp() {
        this.banner = new Banner();
        this.context = new ClassPathXmlApplicationContext("/context-ais.xml");
        this.aisConsumer = context.getBean("socketAISConsumer", AISConsumer.class);
    }

    public static void main(String[] args) {
        new MainApp().run();
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
