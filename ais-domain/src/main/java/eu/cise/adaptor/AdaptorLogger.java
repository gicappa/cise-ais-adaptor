package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.DispatchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AdaptorLogger {

    void logDispatchResponses(DispatchResult result);

    void logDispatchError(Throwable throwable);

    class Slf4j implements AdaptorLogger {

        Logger logger = LoggerFactory.getLogger(AdaptorLogger.class);

        @Override
        public void logDispatchResponses(DispatchResult result) {
            logger.trace(result.getResult());
        }

        @Override
        public void logDispatchError(Throwable throwable) {
            logger.error("dispatching error", throwable);
        }
    }
}
