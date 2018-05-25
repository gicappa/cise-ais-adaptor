package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISSource;
import eu.cise.adaptor.InputStreamToStream;
import eu.cise.adaptor.exceptions.AISAdaptorException;

import java.io.IOException;
import java.net.*;
import java.util.stream.Stream;

public class AISSocketSource implements AISSource {

    private final SocketAddress socketAddress;

    public AISSocketSource(String host, Integer port) throws UnknownHostException {
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
            throw new AISAdaptorException(e);
        }
    }
}

