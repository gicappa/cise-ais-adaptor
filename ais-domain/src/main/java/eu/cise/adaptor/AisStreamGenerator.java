package eu.cise.adaptor;

import java.util.stream.Stream;

@FunctionalInterface
public interface AisStreamGenerator {
    Stream<String> generate();
}
