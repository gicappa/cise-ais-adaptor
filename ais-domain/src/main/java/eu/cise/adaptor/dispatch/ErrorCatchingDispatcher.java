package eu.cise.adaptor.dispatch;

import eu.cise.adaptor.Dispatcher;
import eu.cise.servicemodel.v1.message.Message;

/**
 * The is a decorator to the generic dispatcher that wraps the runtime exceptions
 * not to have exit the processing flow in case of errors.
 */
public class ErrorCatchingDispatcher implements Dispatcher {

    private final Dispatcher proxy;

    /**
     * Decorator constructor accepting the base class as a collaborator
     * @param proxy
     */
    public ErrorCatchingDispatcher(Dispatcher proxy) {
        this.proxy = proxy;
    }

    /**
     * The send message wraps the proxied send message in a try catch clause
     * to avoid throwing runtime exceptions.
     *
     * @param message message to be sent
     * @param address gateway address to send the message to
     * @return
     */
    @Override
    public DispatchResult send(Message message, String address) {
        try {
            return proxy.send(message, address);
        } catch (Throwable t) {
            return new DispatchResult(false, t.getMessage());
        }
    }
}
