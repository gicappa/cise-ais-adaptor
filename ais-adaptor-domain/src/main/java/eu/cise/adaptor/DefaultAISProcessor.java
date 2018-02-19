package eu.cise.adaptor;


import jrc.cise.gw.communication.DispatchResult;
import jrc.cise.gw.communication.Dispatcher;

public class DefaultAISProcessor implements AISProcessor {

    private final Translator translator;
    private final Dispatcher dispatcher;

    public DefaultAISProcessor(Translator translator, Dispatcher dispatcher) {
        this.translator = translator;
        this.dispatcher = dispatcher;
    }

    @Override
    public void process(AISMsg message) {
        translator.translate(message)
                .map(m -> dispatcher.send(m, "http://10.1.1.100:8080/messages"))
                .orElse(DispatchResult.success());
    }
}
