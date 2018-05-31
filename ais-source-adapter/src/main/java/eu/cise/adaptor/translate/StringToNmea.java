package eu.cise.adaptor.translate;

import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.exceptions.AdaptorException;

// TODO Add Logging
public class StringToNmea implements Translator<String, NMEAMessage> {

    @Override
    public NMEAMessage translate(String nmeaString) {
        try {
            return NMEAMessage.fromString(nmeaString);
        } catch (Exception e) {
            throw new AdaptorException("string to NMEA translation error", e);
        }
    }
}
