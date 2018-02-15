package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import eu.cise.adaptor.InternalAISMessage;
import eu.cise.adaptor.NavigationStatus;

import java.util.Optional;

/**
 * This classes normalize the AISMessage class read by the tbsalling's library
 * into an internal one.
 *
 */
public class Normalizer {

    public InternalAISMessage normalize(AISMessage m) {
        InternalAISMessage.Builder b = new InternalAISMessage.Builder(m.getMessageType().getCode());

        // TODO the remaining fields are not supported by other type of messages
        // than message type 1,2,3
        if (b.getMessageType() != 1 && b.getMessageType() != 2 && b.getMessageType() != 3)
            return b;

        b.withMMSI(m.getSourceMmsi().getMMSI());

        b.withLatitude((Float) m.dataFields().getOrDefault("latitude", 0F));
        b.withLongitude((Float) m.dataFields().getOrDefault("longitude", 0F));
        b.withCOG((Float) m.dataFields().getOrDefault("courseOverGround", 0F));
        b.withSOG((Float) m.dataFields().getOrDefault("speedOverGround", 0F));
        b.withTrueHeading((Integer) m.dataFields().getOrDefault("trueHeading", 0));
        b.withNavigationStatus(NavigationStatus.valueOf((String) m.dataFields().get("navigationStatus")));

        // TODO not very sure what to do in case of a missing timestamp
        // * is it possible that the timestamp is missing?
        // * should the message be dropped or not?
        b.withTimestamp(oMeta(m).map(Metadata::getReceived).orElse(null));

        return b;
    }

    private Optional<Metadata> oMeta(AISMessage m) {
        return Optional.ofNullable(m.getMetadata());
    }

}
