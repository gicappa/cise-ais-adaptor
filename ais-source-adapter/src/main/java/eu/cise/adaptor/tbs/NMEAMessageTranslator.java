package eu.cise.adaptor.tbs;

import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.translate.Translator;
import eu.cise.datamodel.v1.entity.Entity;

import java.util.Optional;

// TODO Add Logging
public class NMEAMessageTranslator implements Translator<String, Optional<NMEAMessage>> {

    @Override
    public Optional<NMEAMessage> translate(String nmeaString) {
        try {
            return Optional.of(NMEAMessage.fromString(nmeaString));
        } catch (Exception invalidMessageException) {
            return Optional.empty();
        }
    }
}
