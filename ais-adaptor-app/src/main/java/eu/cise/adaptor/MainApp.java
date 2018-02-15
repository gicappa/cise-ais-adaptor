package eu.cise.adaptor;

import eu.cise.adaptor.tbsalling.FileAISConsumer;

import java.io.IOException;

/**
 * Application entry point
 */
public class MainApp {

    public static final String VERSION = "1.0";
    private final AISConsumer aisConsumer;
    private final Banner banner;

    public static void main(String[] args) {
        new MainApp().run();
    }

    public MainApp() {
        this.banner = new Banner();
        this.aisConsumer = new FileAISConsumer("/aistest.stream.txt");
//        this.aisConsumer = new SocketAISConsumer("139.191.9.35", 60000);
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
