package eu.cise.adaptor.process;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.translate.AISTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger("ais-processor");

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
                            .orElse(DispatchResult.success())
                    , pool);

            DispatchResult result = cf.get();

            if (!result.isOK())
                System.out.println(result.getResult());

            Thread.sleep(config.getProcessingIdleTime());
        } catch (InterruptedException e) {
            throw new AISAdaptorException(e);
        } catch (ExecutionException ee) {
            logger.debug("error", ee);
        }
    }
}
