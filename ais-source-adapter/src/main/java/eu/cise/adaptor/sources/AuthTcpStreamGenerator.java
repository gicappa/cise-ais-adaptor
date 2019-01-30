package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.exceptions.AdaptorException;
import org.aeonbits.owner.ConfigFactory;

import java.io.*;
import java.net.Socket;
import java.util.stream.Stream;

/**
 * This class is used in a simple case of an authentication protocol where the generator needs
 * to connect to a socket under a simple authentication protocol.
 * The protocol is very simple and foresee that after the accept from the TCP server the client
 * adaptor will send a single line on the TCP socket with the credentials (following a template
 * configurable by a property) and wil receive an answer with a single line expressing success
 * or failure of the authentication.
 * <p>
 * In case of success the AIS message stream will start flowing, while in case of failure an
 * exception will be thrown.
 */
@SuppressWarnings("unused")
public class AuthTcpStreamGenerator implements AisStreamGenerator {

    private static final AuthTcpAdaptorConfig config
            = ConfigFactory.create(AuthTcpAdaptorConfig.class);
    private final AisTcpStreamGenerator decorated;
    private final PrintWriter writer;
    private final Socket socket;

    public AuthTcpStreamGenerator() {
        this(config.getAISSourceSocketHost(), config.getAISSourceSocketPort(), new Socket());
    }

    /**
     * Construct the object, building an internal { AisTcpStreamGenerator }
     * NOTE: this class could be more abstract and decorating a generic AisStreamGenerator if the
     * latter could expose more abstraction and specialization. Before starting generalizing, I'd
     * like to keep it simpler and add the generalization needed evaluating case by case.
     *
     * @param host   the host listening for connections
     * @param port   the port opened to receive connections
     * @param socket the socket object to use to open the connection
     */
    public AuthTcpStreamGenerator(String host, Integer port, Socket socket) {
        try {
            this.decorated = new AisTcpStreamGenerator(host, port, socket);
            this.socket = socket;
            this.writer = getWriter(socket);
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    /**
     * Decorates the current {AisTcpStreamGenerator} sending to the socket a login line with a
     * carriage return at the end with the credential formatted accordingly to the
     * ais-source.login.request.template in the property file ais-adaptor.properties
     * The first '%s' will be replaced by the username and the second %s will be replaced by the
     * password.
     *
     * @return a stream of String of AIS messages, if the authentication is granted.
     */
    @Override
    public Stream<String> generate() {
        try {
            writer.println(
                    loginCommand(config.getTcpLoginUsername(), config.getTcpLoginPassword()));

            String input = readAuthResponse(config.getTcpLoginSuccessTemplate().length());

            if (!input.equalsIgnoreCase(config.getTcpLoginSuccessTemplate()))
                throw new AdaptorException("ais-source-adapter|auth_error|received[" + input + "]");

            return decorated.generate();
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    /**
     * A method to read a string coming directly fom the socket InputStream.
     *
     * @param numOfBytes the number of byte to be read
     * @return the string with the content read in the socket
     * @throws IOException is thrown in case of any I/O error with the socket
     */
    private String readAuthResponse(int numOfBytes) throws IOException {
        InputStream is = socket.getInputStream();

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < numOfBytes; i++) {
            buffer.append((char) is.read());
        }
        return buffer.toString();
    }

    /**
     * Format properly a String with a possible login command defined by a username and a password.
     *
     * @param username the login username to be passed to the TCP socket
     * @param password the login password to be passed to the TCP socket
     * @return the formatted command to login in the socket
     */
    private String loginCommand(String username, String password) {
        return config.getTcpLoginRequestTemplate(username, password);
    }

    /**
     * Extract a BufferedReader out of a socket InputStream.
     *
     * @param socket the socket object
     * @return the BufferedReader bound to the socket
     * @throws IOException in case of a socket error
     */
    private BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Extract a PrintWriter out of a socket OutputStream.
     *
     * @param socket the socket object
     * @return the PrintWriter bound to the socket
     * @throws IOException in case of a socket error
     */
    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }
}
