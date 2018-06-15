package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import eu.cise.adaptor.exceptions.AdaptorException;

import java.io.IOException;
import java.net.*;
import java.util.stream.Stream;

public class AisSocketSource implements AisStreamGenerator {

    private final SocketAddress socketAddress;

    public AisSocketSource(String host, Integer port) throws UnknownHostException {
        this.socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
    }

    /**
     * TODO close socket
     *
     * @return the input stream
     */
    public Stream<String> generate() {
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress);
            return new InputStreamToStream().stream(socket.getInputStream());
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }
}

