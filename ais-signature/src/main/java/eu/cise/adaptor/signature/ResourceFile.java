package eu.cise.adaptor.signature;

import java.io.InputStream;

public class ResourceFile {

    private final String name;

    public ResourceFile(String name) {
        this.name = name;
    }

    public InputStream getStream() {
        return this.getClass().getResourceAsStream("/" + name);
    }
}
