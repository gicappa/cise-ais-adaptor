package eu.cise.adaptor.process;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.servicemodel.v1.message.Push;

public interface UseCaseMapAISToCISE {
    Push map(AISMsg msg);

    DispatchResult process(AISMsg message);
}
