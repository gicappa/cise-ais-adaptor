package eu.cise.adaptor.tbs;

import dk.tbsalling.aismessages.AISInputStreamReader;
import eu.cise.adaptor.AISMessageConsumer;
import eu.cise.adaptor.AISSource;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import org.aeonbits.owner.ConfigFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class FileAISSource implements AISSource {

    private final InputStream inputStream;

    public FileAISSource() {
        FileAISSourceConfig config = ConfigFactory.create(FileAISSourceConfig.class);

        if (config.getAISSourceFilename() == null)
            throw new AISAdaptorException("ais-source.file.name property is not \n" +
                    "specified in the file ais-adaptor.properties");

        inputStream = FileAISSource.class.getClassLoader().getResourceAsStream(config.getAISSourceFilename());

        if (inputStream == null)
            throw new AISAdaptorException("ais-source.file.name property set to \n" +
                    "a not existing filename: " + config.getAISSourceFilename());
    }

    @Override
    public <T> void startConsuming(AISMessageConsumer<T> consumer) {
        try {
            AISInputStreamReader aisStream = new AISInputStreamReader(inputStream, (Consumer) consumer);
            aisStream.run();
        } catch (IOException e) {
            throw new AISAdaptorException(e);
        }
    }
}
