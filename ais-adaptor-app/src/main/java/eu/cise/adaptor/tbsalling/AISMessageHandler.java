package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;

import java.util.function.Consumer;

public class AISMessageHandler implements Consumer<AISMessage> {

    @Override
    public void accept(AISMessage t) {
        System.out.println("Received AIS message from MMSI " + t.getSourceMmsi().getMMSI() + ": " + t.getMessageType());
        System.out.println(t.dataFields());
    }

}
