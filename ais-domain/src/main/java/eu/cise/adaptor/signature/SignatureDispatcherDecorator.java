package eu.cise.adaptor.signature;

import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.servicemodel.v1.message.Message;

public class SignatureDispatcherDecorator implements Dispatcher {

    private final Dispatcher dispatcher;
    private final SignatureService signatureService;

    public SignatureDispatcherDecorator(Dispatcher dispatcher, SignatureService signatureService) {
        this.dispatcher = dispatcher;
        this.signatureService = signatureService;
    }

    @Override
    public DispatchResult send(Message message, String address) {
        return dispatcher.send(signatureService.sign(message), address);
    }
}
