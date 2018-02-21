package eu.cise.adaptor;

import eu.cise.adaptor.tbsalling.FileAISConsumer;
import eu.cise.adaptor.tbsalling.SocketAISConsumer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final AISConsumer aisConsumer;
    private final Banner banner;
    private final ApplicationContext context;

    public static void main(String[] args) {
        new MainApp().run();
    }

    public MainApp() {
        this.banner = new Banner();
        this.context = new ClassPathXmlApplicationContext("/context-ais.xml");
        this.aisConsumer = context.getBean("fileAISConsumer", AISConsumer.class);
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
