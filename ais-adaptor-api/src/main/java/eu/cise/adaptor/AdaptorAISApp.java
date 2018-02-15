package eu.cise.adaptor;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.nmea.NMEAMessageHandler;
import dk.tbsalling.aismessages.nmea.NMEAMessageSocketClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

public class AdaptorAISApp implements Consumer<AISMessage> {

    public static void main(String[] args) {
        new AdaptorAISApp().runDemo();
    }

    @Override
    public void accept(AISMessage aisMessage) {

        System.out.println("Received AIS message: " + aisMessage);
    }

    public void runDemo() {
        System.out.println("AISMessages Demo App");
        System.out.println("--------------------");

        try {
            NMEAMessageSocketClient nmeaMessageHandler = new NMEAMessageSocketClient("139.191.9.35", 60000, new NMEAMessageHandler("DEMOSRC1", this));
            nmeaMessageHandler.run();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

}