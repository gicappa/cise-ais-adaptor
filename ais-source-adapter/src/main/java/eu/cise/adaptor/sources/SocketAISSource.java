package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.translate.utils.InputStreamToStream;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SocketAISSource implements AisStreamGenerator {

    private final String host;
    private final int port;

    public SocketAISSource(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Stream generate() {
        return new InputStreamToStream().stream(null);
    }
}