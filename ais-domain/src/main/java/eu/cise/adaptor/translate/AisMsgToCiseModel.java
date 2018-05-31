package eu.cise.adaptor.translate;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.adaptor.AisMsg;
import eu.cise.datamodel.v1.entity.Entity;

import java.util.Optional;

public class AisMsgToCiseModel implements Translator<AisMsg, Optional<Entity>> {

    private final Message5Translator message5Translator;
    private final Message123Translator message123Translator;

    public AisMsgToCiseModel(AdaptorConfig config) {
        message123Translator = new Message123Translator(config);
        message5Translator = new Message5Translator();
    }

    @Override
    public Optional<Entity> translate(AisMsg message) {
        if (isMessageOfType123(message))
            return Optional.of(message123Translator.translate(message));
        else if (isMessageOfType5(message))
            return Optional.of(message5Translator.translate(message));

        return Optional.empty();
    }

    private boolean isMessageOfType5(AisMsg message) {
        return message.getMessageType() == 5;
    }

    private boolean isMessageOfType123(AisMsg message) {
        return message.getMessageType() == 1 ||
                message.getMessageType() == 2 ||
                message.getMessageType() == 3;
    }

}
