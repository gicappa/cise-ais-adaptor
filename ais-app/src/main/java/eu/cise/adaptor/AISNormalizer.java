package eu.cise.adaptor;

import dk.tbsalling.aismessages.ais.messages.AISMessage;

/**
 * What a normalizer does is to abstract from the AIS library used to read
 * the AIS streams.
 *
 * The normalizer will take as an input an {@link AISMessage}, that is the
 * representation of an AIS message coming from the tbsalling's library and
 * normalize it to a {@link AISMsg} that is a domain object.
 */
public interface AISNormalizer {
    AISMsg normalize(AISMessage m);
}
