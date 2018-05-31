package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisSource;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import eu.cise.adaptor.exceptions.AdaptorException;

import java.io.IOException;
import java.net.*;
import java.util.stream.Stream;

public class AisSocketSource implements AisSource {

    private final SocketAddress socketAddress;

    public AisSocketSource(String host, Integer port) throws UnknownHostException {
        this.socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
    }

    /**
     * TODO close socket
     *
     * @return the input stream
     * @throws IOException the exception for a socket not connected
     */
    public Stream<String> open() {
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress);
            return new InputStreamToStream().stream(socket.getInputStream());
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }
}

