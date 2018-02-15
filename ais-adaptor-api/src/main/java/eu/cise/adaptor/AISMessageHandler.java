package eu.cise.adaptor;

import dk.tbsalling.aismessages.ais.messages.AISMessage;

import java.util.function.Consumer;

public class AISMessageHandler<T extends AISMessage> implements Consumer<T> {


    @Override
    public void accept(T t) {
        System.out.println("Received AIS message from MMSI " + t.getSourceMmsi().getMMSI() + ": " + t.getMessageType());
        System.out.println(t.dataFields());
    }

    @Override
    public Consumer<T> andThen(Consumer<? super T> after) {
        return this;
    }
}
