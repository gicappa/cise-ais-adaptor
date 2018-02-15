package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.InternalAISMessage;

/**
 * This classes normalize the AISMessage class read by the tbsalling's library
 * into an internal one.
 *
 */
public class Normalizer {

    public InternalAISMessage normalize(AISMessage m) {
        return new InternalAISMessage.Builder(m.getMessageType().getCode());
    }

}
