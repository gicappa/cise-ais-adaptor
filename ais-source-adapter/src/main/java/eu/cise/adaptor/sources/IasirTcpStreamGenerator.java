package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.exceptions.AdaptorException;
import org.aeonbits.owner.ConfigFactory;

import java.io.*;
import java.net.Socket;
import java.util.stream.Stream;

import static java.lang.String.format;

@SuppressWarnings("unused")
public class IasirTcpStreamGenerator implements AisStreamGenerator {

    private static final String LOGIN_TEMPLATE = "[TYPE=LOGIN;username=%s;password=%s]";
    private final Socket socket;
    private final AisTcpStreamGenerator decorated;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final IsairTcpAdaptorConfig config;

    public IasirTcpStreamGenerator() {
        try {
            this.config = ConfigFactory.create(IsairTcpAdaptorConfig.class);
            this.socket = new Socket();
            this.decorated = new AisTcpStreamGenerator(
                    config.getAISSourceSocketHost(),
                    config.getAISSourceSocketPort(),
                    socket);
            this.reader = getReader(socket);
            this.writer = getWriter(socket);
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    public IasirTcpStreamGenerator(String host, Integer port, Socket socket) {
        try {
            this.config = ConfigFactory.create(IsairTcpAdaptorConfig.class);
            this.socket = socket;
            this.decorated = new AisTcpStreamGenerator(host, port, socket);
            this.reader = getReader(socket);
            this.writer = getWriter(socket);
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    @Override
    public Stream<String> generate() {
        try {
            writer.println(loginCommand(config.getIsairUsername(), config.getIsairPassword()));
            String input = reader.readLine();

            if (input == null || !input.equalsIgnoreCase("[TYPE=LOGIN_OK]"))
                throw new AdaptorException("ISAIR Authentication error: " + input);

            return decorated.generate();
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    private String loginCommand(String username, String password) {
        return format(LOGIN_TEMPLATE, username, password);
    }
}
