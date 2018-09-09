package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import org.aeonbits.owner.ConfigFactory;

import java.io.IOException;
import java.net.*;
import java.util.stream.Stream;

/**
 * This stream generator connects to a TCP sockets and reads line by line the
 * AIS Message information in a textual format. Each message is separated by the
 * others through a line feed (LF) character and as soon as is read is sent to
 * the {@link java.util.stream.Stream} of {@link String}.
 */
@SuppressWarnings("unused")
public class AisTcpStreamGenerator implements AisStreamGenerator {

    private final SocketAddress socketAddress;
    private final AisTcpAdaptorConfig config = ConfigFactory.create(AisTcpAdaptorConfig.class);

    /**
     * Constructing the class reading host and port from the configuration.
     *
     * @throws UnknownHostException when the hostname is not recognised.
     */
    public AisTcpStreamGenerator() throws UnknownHostException {
        AisTcpAdaptorConfig config = ConfigFactory.create(AisTcpAdaptorConfig.class);
        this.socketAddress = new InetSocketAddress(InetAddress.getByName(config.getAISSourceSocketHost()), config.getAISSourceSocketPort());
    }

    /**
     * Constructing the class specifying which server host and port to connect to.
     *
     * @param host hostname or ip address where the TCP socket should be opened.
     * @param port port to open to get the AIS messages information.
     * @throws UnknownHostException when the hostname is not recognised.
     */
    public AisTcpStreamGenerator(String host, Integer port) throws UnknownHostException {
        this.socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
    }

    /**
     * @return a Stream of Strings each of them containing an AIS message
     */
    public Stream<String> generate() {
        try (Socket socket = new Socket()) {
            socket.connect(socketAddress);
            return new InputStreamToStream().stream(socket.getInputStream());
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }
}

