package eu.cise.adaptor.dispatch;

import eu.cise.adaptor.Dispatcher;
import eu.cise.servicemodel.v1.message.Message;

public class ErrorCatchingDispatcher implements Dispatcher {

    private final Dispatcher proxy;

    public ErrorCatchingDispatcher(Dispatcher proxy) {
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
