package eu.cise.adaptor;

import java.io.InputStream;
import java.util.stream.Stream;

@FunctionalInterface
public interface AISSource {
    Stream<String> open();
}
