package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISSource;
import eu.cise.adaptor.InputStreamToStream;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import org.aeonbits.owner.ConfigFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

public class FileAISSource implements AISSource {

    private final FileAISSourceConfig config;

    public FileAISSource() {
        config = ConfigFactory.create(FileAISSourceConfig.class);

        if (config.getAISSourceFilename() == null)
            throw new AISAdaptorException("The 'ais-source.file.name' property is not " +
                    "set in the ais-adaptor.properties file");
        }

    public InputStream open(String filename) {
        InputStream is = openFile(filename);

        if (is != null)
            return is;

        return openResource(filename);
    }

    public InputStream openFile(String filename) {
        return FileAISSource.class.getClassLoader().getResourceAsStream(filename);
    }

    public InputStream openResource(String filename) {
        try {
            return new FileInputStream(System.getProperty("conf.dir") + filename);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public Stream<String> open() {
        InputStream inputStream = open(config.getAISSourceFilename());

        if (inputStream == null) {
            throw new AISAdaptorException("The file '" + config.getAISSourceFilename() +
                    "' does not exists neither in the /conf/ directory nor in the classpath");
        }

        return new InputStreamToStream().stream(inputStream);
    }
}
