package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.AISInputStreamReader;
import eu.cise.adaptor.AISAdaptorException;
import eu.cise.adaptor.AISConsumer;
import eu.cise.adaptor.tbsalling.AISMessageHandler;

import java.io.IOException;
import java.io.InputStream;

public class FileAISConsumer implements AISConsumer {

    private final AISInputStreamReader aisStream;

    public FileAISConsumer(String filename) {
        InputStream inputStream = getClass().getResourceAsStream(filename);
        aisStream = new AISInputStreamReader(inputStream, new AISMessageHandler());
    }

    public void run() {
        try {
            aisStream.run();
        } catch (IOException e) {
            throw new AISAdaptorException();
        }
    }

}
