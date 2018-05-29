package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISSource;
import eu.cise.adaptor.InputStreamToStream;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SocketAISSource implements AISSource {

    private final String host;
    private final int port;

    public SocketAISSource(String host, int port) {
        this.host = host;
        this.port = port;
    }

//    public <AISMessage> void startConsuming(AISMessageConsumer<AISMessage> consumer) {
//        try {
//            NMEAMessageSocketClient nmeaMessageHandler = new NMEAMessageSocketClient(
//                    host, port, new NMEAMessageHandler("AISAdaptor", (Consumer) consumer)
//            );
//
//            nmeaMessageHandler.run();
//        } catch (IOException e) {
//            throw new AISAdaptorException(e);
//        }
//    }

    @Override
    public Stream open() {
        return new InputStreamToStream().stream(null);
    }
}