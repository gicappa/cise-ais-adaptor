package eu.cise.adaptor.dispatch;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.process.AISProcessor;
import eu.cise.servicemodel.v1.message.Message;

import java.util.ArrayList;

public class DelayedProcessor implements AISProcessor {
    private final Dispatcher dispatcher;
    private final ArrayList<Message> invocations;

    public DelayedProcessor(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.invocations = new ArrayList<>();
    }

    //    @Override
    public DispatchResult send(Message message, String address) {
        if (invocations.size() > 2)
            return dispatcher.send(message, address);

        return null;
    }

    @Override
    public DispatchResult process(AISMsg message) {
        return null;
//        new BufferedReader().lines

    }
}
