package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.datamodel.v1.entity.Entity;

public class AisMsgToCiseModel implements Translator<AISMsg, Entity> {

    private final Message5Translator message5Translator;
    private final Message123Translator message123Translator;

    public AisMsgToCiseModel(AISAdaptorConfig config) {
        message123Translator = new Message123Translator(config);
        message5Translator = new Message5Translator();
    }

    @Override
    public Entity translate(AISMsg message) {
        if (isMessageOfType123(message))
            return message123Translator.translate(message);
        else if (isMessageOfType5(message))
            return message5Translator.translate(message);

        throw new AISAdaptorException("ais message not supported");
    }

    private boolean isMessageOfType5(AISMsg message) {
        return message.getMessageType() == 5;
    }

    private boolean isMessageOfType123(AISMsg message) {
        return message.getMessageType() == 1 ||
                message.getMessageType() == 2 ||
                message.getMessageType() == 3;
    }

}
