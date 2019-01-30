/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
public class AisMsgToVessel implements Translator<AisMsg, Optional<Entity>> {

    private final Message5Translator message5Translator;
    private final Message123Translator message123Translator;

    /**
     * Constructor accepting the config as a collaborator.
     * <p>
     * todo the constructor is creating objects and it should be done in the
     * main partition. A better design would foresee a factory object using
     * the correct implementation of the given message. Another way to implement
     * it could be through a chain of responsibility.
     *
     * @param config the adaptor config collaborator
     */
    public AisMsgToVessel(AdaptorConfig config) {
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
        try {
            if (isMessageOfType123(message))
                return Optional.of(message123Translator.translate(message));
            else if (isMessageOfType5(message))
                return Optional.of(message5Translator.translate(message));

            return Optional.empty();
        } catch (Exception e) {
            // if it's not able to translate the message just skip it
            return Optional.empty();
        }
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
