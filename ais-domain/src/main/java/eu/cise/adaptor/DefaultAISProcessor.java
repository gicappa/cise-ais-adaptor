package eu.cise.adaptor;


import jrc.cise.gw.sending.DispatchResult;
import jrc.cise.gw.sending.Dispatcher;

public class DefaultAISProcessor implements AISProcessor {

    private final AISTranslator translator;
    private final Dispatcher dispatcher;
    private final AISAdaptorConfig config;

    public DefaultAISProcessor(AISTranslator translator, Dispatcher dispatcher, AISAdaptorConfig config) {
        this.translator = translator;
        this.dispatcher = dispatcher;
        this.config = config;
    }

    @Override
    public void process(AISMsg message) {
        try {
            System.out.print(".");

            DispatchResult result = translator.translate(message)
                    .map(m -> dispatcher.send(m, config.getGatewayAddress()))
                    .orElse(DispatchResult.success());

            if (!result.isOK())
                System.out.println(result.getResult());

            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new AISAdaptorException(e);
        }
    }
}
