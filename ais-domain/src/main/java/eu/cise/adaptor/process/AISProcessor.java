package eu.cise.adaptor.process;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.dispatch.DispatchResult;

@FunctionalInterface
public interface AISProcessor {
    DispatchResult process(AISMsg message);
}
