package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisSource;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import eu.cise.adaptor.exceptions.AdaptorException;
import org.aeonbits.owner.ConfigFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

public class FileAisSource implements AisSource {

    private final FileAisSourceConfig config;

    public FileAisSource() {
        config = ConfigFactory.create(FileAisSourceConfig.class);

        if (config.getAISSourceFilename() == null)
            throw new AdaptorException("The 'ais-source.file.name' property is not " +
                    "set in the ais-adaptor.properties file");
        }

    public InputStream open(String filename) {
        InputStream is = openFile(filename);

        if (is != null)
            return is;

        return openResource(filename);
    }

    public InputStream openFile(String filename) {
        return FileAisSource.class.getClassLoader().getResourceAsStream(filename);
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
            throw new AdaptorException("The file '" + config.getAISSourceFilename() +
                    "' does not exists neither in the /conf/ directory nor in the classpath");
        }

        return new InputStreamToStream().stream(inputStream);
    }
}
