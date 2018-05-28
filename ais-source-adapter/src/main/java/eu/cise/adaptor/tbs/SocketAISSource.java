package eu.cise.adaptor.tbs;

import dk.tbsalling.aismessages.nmea.NMEAMessageHandler;
import dk.tbsalling.aismessages.nmea.NMEAMessageSocketClient;
import eu.cise.adaptor.AISMessageConsumer;
import eu.cise.adaptor.AISSource;
import eu.cise.adaptor.InputStreamToStream;
import eu.cise.adaptor.exceptions.AISAdaptorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SocketAISSource implements AISSource {

    private final String host;
    private final int port;

    public SocketAISSource(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public <AISMessage> void startConsuming(AISMessageConsumer<AISMessage> consumer) {
        try {
            NMEAMessageSocketClient nmeaMessageHandler = new NMEAMessageSocketClient(
                    host, port, new NMEAMessageHandler("AISAdaptor", (Consumer) consumer)
            );

            nmeaMessageHandler.run();
        } catch (IOException e) {
            throw new AISAdaptorException(e);
        }
    }

    @Override
    public Stream open() {
        return new InputStreamToStream().stream(null);
    }
}