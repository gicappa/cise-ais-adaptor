package eu.cise.adaptor.dispatch;

import eu.cise.adaptor.Dispatcher;
import eu.cise.servicemodel.v1.message.Message;

public class CatchingDispatcher implements Dispatcher {

    private final Dispatcher proxy;

    public CatchingDispatcher(Dispatcher proxy) {
        this.proxy = proxy;
    }

    @Override
    public DispatchResult send(Message message, String address) {
        try {
            return proxy.send(message, address);
        } catch (Throwable t) {
            return new DispatchResult(false, t.getMessage());
        }
    }
}
