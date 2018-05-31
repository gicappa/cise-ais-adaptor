package eu.cise.adaptor.translate;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.exceptions.AdaptorException;

import java.util.ArrayList;

public class NmeaToAISMessage implements Translator<NMEAMessage, AISMessage> {


    private final String source;
    private final ArrayList<NMEAMessage> messageFragments = new ArrayList<>();

    public NmeaToAISMessage(String source) {
        this.source = source;
    }

    @Override
    public AISMessage translate(NMEAMessage nmeaMessage) {
        if (!nmeaMessage.isValid()) {
            throw new AdaptorException("NMEA to AISMessage transformation error");
        }

        int numberOfFragments = nmeaMessage.getNumberOfFragments();
        if (numberOfFragments <= 0) {
            messageFragments.clear();
            return null;
        }

        if (numberOfFragments == 1) {
            messageFragments.clear();
            return AISMessage.create(new Metadata(source), nmeaMessage);
        }

        int fragmentNumber = nmeaMessage.getFragmentNumber();
        if (fragmentNumber < 0) {
            messageFragments.clear();
            return null;
        }

        if (fragmentNumber > numberOfFragments) {
            messageFragments.clear();
            return null;
        }

        int expectedFragmentNumber = messageFragments.size() + 1;
        if (expectedFragmentNumber != fragmentNumber) {
            messageFragments.clear();
            return null;
        }

        messageFragments.add(nmeaMessage);

        if (nmeaMessage.getNumberOfFragments() == messageFragments.size()) {
            AISMessage aisMessage = AISMessage.create(new Metadata(source), messageFragments.toArray(new NMEAMessage[messageFragments.size()]));

            messageFragments.clear();
            return aisMessage;
        }

        return null;
    }
}