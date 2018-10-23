package eu.cise.adaptor.sign11;

import eu.cise.adaptor.exceptions.AdaptorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceFile {

    private final String name;

    public ResourceFile(String name) {
        this.name = name;
    }

    public InputStream getStream() {
        InputStream is;

        is = openFromFilesystem();
        if (is != null)
            return is;

        is = openFromClasspath();
        if (is != null)
            return is;

        throw new AdaptorException("Resource not found: " + name);
    }

    private InputStream openFromFilesystem() {
        try {
            return new FileInputStream(filePath());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private String filePath() {
        return confDir() + File.separator + name;
    }

    private String confDir() {
        return System.getProperty("conf.dir");
    }

    private InputStream openFromClasspath() {
        return this.getClass().getResourceAsStream("/" + name);
    }
}
