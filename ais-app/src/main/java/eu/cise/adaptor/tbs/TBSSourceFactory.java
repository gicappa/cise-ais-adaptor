package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISMessageConsumer;
import eu.cise.adaptor.AISSource;
import eu.cise.adaptor.AISSourceFactory;

//"/raw-ais/nmea-sample"
public class TBSSourceFactory implements AISSourceFactory {

    private final AISMessageConsumer consumer;

    public TBSSourceFactory(AISMessageConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public AISSource newFileSource(String filename) {
        return new FileAISSource(filename, consumer);
    }

    @Override
    public AISSource newSocketSource(String host, int port) {
        return new SocketAISSource(host, port, consumer);
    }
}
