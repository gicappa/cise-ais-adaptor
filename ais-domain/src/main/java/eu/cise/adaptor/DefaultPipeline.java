/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
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

package eu.cise.adaptor;

import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.servicemodel.v1.message.Message;
import java.util.Optional;
import reactor.core.publisher.Flux;

/**
 * An AIS Stream Processor object is meant to transform a string of NMEA messages into a Flux of
 * messages. Flux is a class of Reactor library and is meant to handle a stream of information
 * giving the possibility to handle simply different parameters like buffering of the information (a
 * number of objects of the stream to be collected before sending a message) backpressure strategy
 * (how to handle a congestion of messages coming from the source of information) and so on.
 */
public class DefaultPipeline implements Pipeline<String, Message> {

    private final StringToAisMsg stringToAisMsg;
    private final AisMsgToVessel aisMsgToVessel;
    private final VesselToPushMessage vesselToPushMessage;
    private final AdaptorConfig config;

    /**
     * The constructor will use these collaborators to streamline the transformation from NMEA to
     * CISE messages.
     *
     * @param stringToAisMsg      transformer from string to ais
     * @param aisMsgToVessel      transformer from ais to vessel
     * @param vesselToPushMessage transformer from vessel to push
     * @param config              configuration object
     */
    public DefaultPipeline(StringToAisMsg stringToAisMsg,
        AisMsgToVessel aisMsgToVessel,
        VesselToPushMessage vesselToPushMessage,
        AdaptorConfig config) {

        this.stringToAisMsg = stringToAisMsg;
        this.aisMsgToVessel = aisMsgToVessel;
        this.vesselToPushMessage = vesselToPushMessage;
        this.config = config;
    }

    /**
     * The pipeline will process NMEA message strings into a number of CISE push messages.
     *
     * @param nmeaStringFlux the flux of NMEA strings
     * @return a flux of CISE push message objects.
     */
    @Override
    public Flux<Message> process(Flux<String> nmeaStringFlux) {
        return toPushMessageFlux(stringToAisMsg.translate(nmeaStringFlux));
    }

    Flux<Message> toPushMessageFlux(Flux<AisMsg> aisMsgFlux) {
        return aisMsgFlux.map(aisMsgToVessel::translate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .bufferTimeout(config.getNumberOfEntitiesPerMessage(),
                config.getEntityBufferTimeout())
            .map(vesselToPushMessage::translate);
    }
}
