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
        InternalAISMessage.Builder b = new InternalAISMessage.Builder(m.getMessageType().getCode());

        b.withLatitude((Float) m.dataFields().getOrDefault("latitude", 0F));
        b.withLongitude((Float) m.dataFields().getOrDefault("longitude", 0F));
        b.withMMSI(m.getSourceMmsi().getMMSI());
        return b;
    }

}
