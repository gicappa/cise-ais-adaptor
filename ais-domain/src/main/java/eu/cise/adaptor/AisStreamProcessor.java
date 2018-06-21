package eu.cise.adaptor;

import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.servicemodel.v1.message.Message;
import reactor.core.publisher.Flux;

import java.util.Optional;

/**
 * An AIS Stream Processor object is meant to transform a string of NMEA messages
 * into a Flux of messages. Flux is a class of Reactor library and is meant to
 * handle a stream of information giving the possibility to handle simply
 * different parameters like buffering of the information (a number of objects
 * of the stream to be collected before sending a message) backpressure strategy
 * (how to handle a congestion of messages coming from the source of information)
 * and so on.
 */
public class AisStreamProcessor {

    private final StringToAisMsg stringToAisMsg;
    private final AisMsgToVessel aisMsgToVessel;
    private final VesselToPushMessage vesselToPushMessage;
    private final AdaptorConfig config;

    /**
     * The constructor will use these collaborators to streamline the
     * transformation from NMEA to CISE messages.
     *
     * @param stringToAisMsg
     * @param aisMsgToVessel
     * @param vesselToPushMessage
     * @param config
     */
    public AisStreamProcessor(StringToAisMsg stringToAisMsg,
                              AisMsgToVessel aisMsgToVessel,
                              VesselToPushMessage vesselToPushMessage,
                              AdaptorConfig config) {

        this.stringToAisMsg = stringToAisMsg;
        this.aisMsgToVessel = aisMsgToVessel;
        this.vesselToPushMessage = vesselToPushMessage;
        this.config = config;
    }

    Flux<Message> process(Flux<String> aisStringFlux) {
        return toPushMessageFlux(stringToAisMsg.translate(aisStringFlux));
    }

    Flux<Message> toPushMessageFlux(Flux<AisMsg> aisMsgFlux) {
        return aisMsgFlux.map(aisMsgToVessel::translate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .buffer(config.getNumberOfEntitiesPerMessage())
                .map(vesselToPushMessage::translate);

    }
}
