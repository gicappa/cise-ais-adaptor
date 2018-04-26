package eu.cise.adaptor.process;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.translate.AISTranslator;
import jrc.cise.gw.sending.DispatchResult;
import jrc.cise.gw.sending.Dispatcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class DefaultAISProcessor implements AISProcessor {

    private final AISTranslator translator;
    private final Dispatcher dispatcher;
    private final AISAdaptorConfig config;
    private final ExecutorService pool;

    public DefaultAISProcessor(AISTranslator translator, Dispatcher dispatcher, AISAdaptorConfig config) {
        this.translator = translator;
        this.dispatcher = dispatcher;
        this.config = config;
        this.pool = Executors.newFixedThreadPool(10);

    }

    @Override
    public void process(AISMsg message) {
        try {
            CompletableFuture<DispatchResult> cf = supplyAsync(
                    () -> translator.translate(message)
                            .map(m -> dispatcher.send(m, config.getGatewayAddress()))
                            .orElse(DispatchResult.success()
                            )
                    , pool);

            DispatchResult result = cf.get();

            if (!result.isOK())
                System.out.println(result.getResult());

            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new AISAdaptorException(e);
        } catch ( ExecutionException e1) {
            System.err.println(e1.getMessage());
        }
    }
}
