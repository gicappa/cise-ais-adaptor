package eu.cise.adaptor;

/**
 * Supported source types are
 *
 * - file
 * - socket
 */
public interface AISSourceFactory {
    AISSource newFileSource(String filename);

    AISSource newSocketSource(String host, int port);
}
