package eu.cise.adaptor;

import java.util.stream.Stream;

/**
 * This abstraction models the case of object generating a Stream of Strings
 * from a generic data source that could be a file, a socket or whatever
 * datasource is producing AIS data.
 * <br/>
 * In the domain of the current module the expected strings are NMEA messages,
 * but this constraint is not described into the interface.
 */
@FunctionalInterface
public interface AisStreamGenerator {

    /**
     * The generate method is a supplier that will know how to access the
     * source of the AIS information and will stream them out towards the next
     * stages.
     *
     * @return a Stream containing NMEA strings.
     */
    Stream<String> generate();
}
