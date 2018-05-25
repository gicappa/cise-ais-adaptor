package eu.cise.adaptor.translate;

import eu.cise.adaptor.AISAdaptorConfig;
import eu.cise.adaptor.AISMsg;
import eu.cise.datamodel.v1.entity.Entity;

public class ModelTranslator implements Translator<AISMsg, Entity> {

    private final Message5Translator message5Translator;
    private final Message123Translator message123Translator;

    public ModelTranslator(AISAdaptorConfig config) {
        message123Translator = new Message123Translator(config);
        message5Translator = new Message5Translator();
    }

    @Override
    public Entity translate(AISMsg aisMsg) {
        if (aisMsg.getMessageType() == 1 ||
                aisMsg.getMessageType() == 2 ||
                aisMsg.getMessageType() == 3)
            return message123Translator.translate(aisMsg);
        else if (aisMsg.getMessageType() == 5)
            return message5Translator.translate(aisMsg);

        return null;
    }

}
