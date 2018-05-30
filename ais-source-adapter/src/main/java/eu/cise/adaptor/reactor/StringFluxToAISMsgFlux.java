package eu.cise.adaptor.reactor;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.datamodel.v1.entity.Entity;
import reactor.core.publisher.Flux;

public class StringFluxToAISMsgFlux implements AISNormalizer {


    private final StringToNmea stringToNmea;
    private final NmeaToAISMessage nmeaToAISMessage;
    private final AISMessageToAISMsg aisMessageToAisMsg;

    public StringFluxToAISMsgFlux() {
        this.stringToNmea = new StringToNmea();
        this.nmeaToAISMessage = new NmeaToAISMessage("SRC");
        this.aisMessageToAisMsg = new AISMessageToAISMsg();
    }

    public StringFluxToAISMsgFlux(StringToNmea stringToNmea,
                                  NmeaToAISMessage nmeaToAISMessage,
                                  AISMessageToAISMsg aisMessageToAISMsg) {

        this.stringToNmea = stringToNmea;
        this.nmeaToAISMessage = nmeaToAISMessage;
        this.aisMessageToAisMsg = aisMessageToAISMsg;
    }

    @Override
    public Flux<AISMsg> translate(Flux<String> stringFlux) {
        return stringFlux
                .map(stringToNmea::translate)
                .map(nmeaToAISMessage::translate)
                .map(aisMessageToAisMsg::translate);
    }
}
