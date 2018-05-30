package eu.cise.adaptor.reactor;

import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.translate.Translator;
import eu.cise.datamodel.v1.entity.Entity;

// TODO Add Logging
public class StringToNmea implements Translator<String, NMEAMessage> {

    @Override
    public NMEAMessage translate(String nmeaString) {
        try {
            return NMEAMessage.fromString(nmeaString);
        } catch (Exception e) {
            throw new AISAdaptorException("string to NMEA translation error", e);
        }
    }
}
