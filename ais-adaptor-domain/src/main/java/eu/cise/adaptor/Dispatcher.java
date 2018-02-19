package eu.cise.adaptor;


import eu.cise.servicemodel.v1.message.Message;

@FunctionalInterface
public interface Dispatcher {
    Result dispatch(Message message);
}
