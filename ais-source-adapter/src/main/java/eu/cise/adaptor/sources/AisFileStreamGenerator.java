package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import org.aeonbits.owner.ConfigFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * This stream generator opens a file and reads it line by line interpreting the
 * AIS message information in a textual format. Each message is separated by the
 * others through a line feed (LF) character and as soon as is read is sent to
 * the {@link java.util.stream.Stream} of {@link String}.
 */
public class AisFileStreamGenerator implements AisStreamGenerator {

    private final AisFileAdaptorConfig config;

    /**
     * Construct the stream generator by reading a file specified in the
     * configuration file.
     * <p>
     * the property {@code ais-source.file.name} specifies the file name that
     * could be found in the filesystem or in the classpath.
     */
    public AisFileStreamGenerator() {
        config = ConfigFactory.create(AisFileAdaptorConfig.class);

        if (config.getAISSourceFilename() == null)
            throw new AdaptorException("The 'ais-source.file.name' property is not " +
                    "set in the ais-adaptor.properties file");
    }

    /**
     * The generate method tries to open an input stream from the file name
     * and returns a {@link java.util.stream.Stream} of strings with the
     * AIS message in every string.
     *
     * @return the Stream with the messages
     * @thorws an {@link AdaptorException} if the file does not exists
     */
    @Override
    public Stream<String> generate() {
        InputStream inputStream = open(config.getAISSourceFilename());

        if (inputStream == null) {
            throw new AdaptorException("The file '" + config.getAISSourceFilename() +
                    "' does not exists neither in the /conf/ directory nor in the classpath");
        }

        return new InputStreamToStream().stream(inputStream);
    }

    /**
     * It checks if there is a resource matching the file name and returns the
     * {@link InputStream} with the data.
     * Otherwise it checks for a file in the filesystem with that name and
     * returns the {@link InputStream) with the data.
     *
     * @param filename is the filename to be opened
     * @return the {@link InputStream} or null if it does not find the file.
     */
    public InputStream open(String filename) {
        InputStream is = openResource(filename);

        if (is != null)
            return is;

        return openFile(filename);
    }

    /**
     * @param filename file name to be opened from the classpath.
     * @return the {@link InputStream} from the resource on null otherwise.
     */
    public InputStream openResource(String filename) {
        return AisFileStreamGenerator.class.getClassLoader().getResourceAsStream(filename);
    }

    /**
     * @param filename file name to be opened from the file system.
     * @return the {@link InputStream} from the resource on null otherwise.
     */
    public InputStream openFile(String filename) {
        try {
            return new FileInputStream(System.getProperty("conf.dir") + filename);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
