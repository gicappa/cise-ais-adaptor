package eu.cise.adaptor.translate;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.adaptor.AisMsg;
import eu.cise.datamodel.v1.entity.Entity;

import java.util.Optional;

/**
 * This translator translate an AisMsg object into a optional Entity model.
 * The entity model is the ancestor class of a Vessel. This class is a part of
 * the chain of transformations to translate a flow of AIS strings into a
 * corresponding number of HTTP requests containing all the vessels information.
 */
public class AisMsgToCiseModel implements Translator<AisMsg, Optional<Entity>> {

    private final Message5Translator message5Translator;
    private final Message123Translator message123Translator;

    /**
     * Constructor accepting the config as a collaborator.
     *
     * @todo the constructor is creating objects and it should be done in the
     * main partition. A better design would foresee a factory object using
     * the correct implementation of the given message. Another way to implement
     * it could be through a chain of responsibility.
     * @param config the adaptor config collaborator
     */
    public AisMsgToCiseModel(AdaptorConfig config) {
        message123Translator = new Message123Translator(config);
        message5Translator = new Message5Translator();
    }

    /**
     * The translate method is using the message as a selector to choose the
     * right translator.
     *
     * @param message the ais message to be translated
     * @return the translated entity
     */
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