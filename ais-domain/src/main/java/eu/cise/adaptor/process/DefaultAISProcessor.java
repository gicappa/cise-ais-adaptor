package eu.cise.adaptor.process;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.adaptor.translate.ModelTranslator;
import eu.cise.adaptor.translate.ServiceTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class DefaultAISProcessor implements AISProcessor {

    private final ModelTranslator modelTranslator;
    private final ServiceTranslator serviceTranslator;
    private final Dispatcher dispatcher;
    private final AISAdaptorConfig config;
    private final ExecutorService pool;
    private final Logger logger = LoggerFactory.getLogger("ais-processor");

    public DefaultAISProcessor(ModelTranslator modelTranslator,
                               ServiceTranslator serviceTranslator,
                               Dispatcher dispatcher,
                               AISAdaptorConfig config) {
        this.modelTranslator = modelTranslator;
        this.serviceTranslator = serviceTranslator;
        this.dispatcher = dispatcher;
        this.config = config;
        this.pool = Executors.newFixedThreadPool(10);

    }

    @Override
    public void process(AISMsg message) {
        try {

            DispatchResult result = supplyAsync(() -> modelTranslator.translate(message)
                            .map(e -> serviceTranslator.translate(e))
                            .map(m -> dispatcher.send(m, config.getGatewayAddress()))
                            .orElse(DispatchResult.success())
                    , pool).get();

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
