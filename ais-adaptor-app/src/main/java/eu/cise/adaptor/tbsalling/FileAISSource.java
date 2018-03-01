package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.AISInputStreamReader;
import eu.cise.adaptor.AISAdaptorException;
import eu.cise.adaptor.AISSource;

import java.io.IOException;
import java.io.InputStream;

public class FileAISSource implements AISSource {

    private final AISInputStreamReader aisStream;

    public FileAISSource(String filename) {
        InputStream inputStream = getClass().getResourceAsStream(filename);
        aisStream = new AISInputStreamReader(inputStream, new AISMessageHandler());
    }

    public void run() {
        try {
            aisStream.run();
        } catch (IOException e) {
            throw new AISAdaptorException(e);
        }
    }

}
