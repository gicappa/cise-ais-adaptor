package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.nmea.NMEAMessageHandler;
import dk.tbsalling.aismessages.nmea.NMEAMessageSocketClient;
import eu.cise.adaptor.AISAdaptorException;
import eu.cise.adaptor.AISSource;

import java.io.IOException;
import java.net.UnknownHostException;

@SuppressWarnings("unused")
public class SocketAISSource implements AISSource {

    private final NMEAMessageSocketClient nmeaMessageHandler;

    public SocketAISSource(String host, int port, AISMessageConsumer aisMessageConsumer) {
        try {
            nmeaMessageHandler = new NMEAMessageSocketClient(
                    host, port,
                    new NMEAMessageHandler("AISAdaptor", aisMessageConsumer)
            );
        } catch (UnknownHostException e) {
            throw new AISAdaptorException(e);
        }
    }

    @Override
    public void run() {
        try {
            nmeaMessageHandler.run();
        } catch (IOException e) {
            throw new AISAdaptorException(e);
        }

    }
}