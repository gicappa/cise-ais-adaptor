package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.servicemodel.v1.message.Push;

import java.util.Optional;

/**
 * This is the translator from the internal AISMsg object to a CISE Push message
 * <p>
 * TODO There is a difference in latitude and longitude between the AIS and the
 * CISE calculation. Here for simplicity it hasn't been taken into account.
 * <p>
 * Please refer to:
 * https://webgate.ec.europa.eu/CITnet/confluence/display/MAREX/AIS+Message+1%2C2%2C3
 */
public class DefaultAISTranslator implements AISTranslator {

    private final ModelTranslator modelTranslator;
    private final ServiceTranslator serviceTranslator;

    public DefaultAISTranslator(
                                ModelTranslator modelTranslator,
                                ServiceTranslator serviceTranslator) {
        this.modelTranslator = modelTranslator;
        this.serviceTranslator = serviceTranslator;
    }

    @Override
    public Optional<Push> translate(AISMsg message) {
        return modelTranslator.translate(message).map(e -> serviceTranslator.translate(e));
    }

}

