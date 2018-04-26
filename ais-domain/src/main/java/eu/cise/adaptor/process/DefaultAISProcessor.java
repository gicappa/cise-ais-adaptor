package eu.cise.adaptor.process;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.translate.AISTranslator;
import jrc.cise.gw.sending.DispatchResult;
import jrc.cise.gw.sending.Dispatcher;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class DefaultAISProcessor implements AISProcessor {

    private final AISTranslator translator;
    private final Dispatcher dispatcher;
    private final AISAdaptorConfig config;
//    private final ExecutorService executor;

    public DefaultAISProcessor(AISTranslator translator, Dispatcher dispatcher, AISAdaptorConfig config) {
        this.translator = translator;
        this.dispatcher = dispatcher;
        this.config = config;
//        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void process(AISMsg message) {
        try {
            System.out.print(".");
            CompletableFuture<DispatchResult> cf = supplyAsync(() -> translator.translate(message)
                    .map(m -> dispatcher.send(m, config.getGatewayAddress()))
                    .orElse(DispatchResult.success()));

            DispatchResult result = cf.get();

            if (!result.isOK())
                System.out.println(result.getResult());

            Thread.sleep(100);
        } catch (InterruptedException | ExecutionException e) {
            //throw new AISAdaptorException(e);
        }
    }
}
