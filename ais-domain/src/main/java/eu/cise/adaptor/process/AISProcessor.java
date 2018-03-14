package eu.cise.adaptor.process;

import eu.cise.adaptor.AISMsg;

public interface AISProcessor {
    void process(AISMsg message);
}
