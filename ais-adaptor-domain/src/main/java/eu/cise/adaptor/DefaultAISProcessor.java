package eu.cise.adaptor;


import jrc.cise.gw.communication.DispatchResult;
import jrc.cise.gw.communication.Dispatcher;

public class DefaultAISProcessor implements AISProcessor {

    private final Translator translator;
    private final Dispatcher dispatcher;
    private final AISAdaptorConfig config;

    public DefaultAISProcessor(Translator translator, Dispatcher dispatcher, AISAdaptorConfig config) {
        this.translator = translator;
        this.dispatcher = dispatcher;
        this.config = config;
    }

    @Override
    public void process(AISMsg message) {
        translator.translate(message)
                .map(m -> dispatcher.send(m, config.getGatewayAddress()))
                .orElse(DispatchResult.success());
    }
}
