package eu.cise.adaptor.tbs;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.translate.Translator;

import java.util.ArrayList;
import java.util.Optional;

public class SimpleNMEAWhatever implements Translator<NMEAMessage, Optional<AISMessage>> {


    private final String source;
    private final ArrayList<NMEAMessage> messageFragments = new ArrayList<>();

    public SimpleNMEAWhatever(String source) {
        this.source = source;
    }

    @Override
    public Optional<AISMessage> translate(NMEAMessage nmeaMessage) {
        if (!nmeaMessage.isValid()) {
            return Optional.empty();
        }

        int numberOfFragments = nmeaMessage.getNumberOfFragments();
        if (numberOfFragments <= 0) {
            messageFragments.clear();
            return Optional.empty();
        }

        if (numberOfFragments == 1) {
            messageFragments.clear();
            return Optional.of(AISMessage.create(new Metadata(source), nmeaMessage));
        }

        int fragmentNumber = nmeaMessage.getFragmentNumber();
        if (fragmentNumber < 0) {
            messageFragments.clear();
            return Optional.empty();
        }

        if (fragmentNumber > numberOfFragments) {
            messageFragments.clear();
            return Optional.empty();
        }

        int expectedFragmentNumber = messageFragments.size() + 1;
        if (expectedFragmentNumber != fragmentNumber) {
            messageFragments.clear();
            return Optional.empty();
        }

        messageFragments.add(nmeaMessage);

        if (nmeaMessage.getNumberOfFragments() == messageFragments.size()) {
            AISMessage aisMessage = AISMessage.create(new Metadata(source), messageFragments.toArray(new NMEAMessage[messageFragments.size()]));

            messageFragments.clear();
            return Optional.of(aisMessage);
        }

        return Optional.empty();
    }
}

