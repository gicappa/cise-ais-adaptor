package eu.cise.adaptor;

import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;

import java.util.Optional;

import static eu.eucise.helpers.PushBuilder.newPush;

public class ToCISETranslator {
    public Optional<Push> translate(AISMsg aisMessage) {
        if (isTypeSupported(aisMessage)) {
            return Optional.empty();
        }

        Push push = newPush()
                .addEntity(new Vessel())
                .build();

        return Optional.of(push);
    }

    private boolean isTypeSupported(AISMsg aisMessage) {
        return aisMessage.getMessageType() != 1 &&
                aisMessage.getMessageType() != 2 &&
                aisMessage.getMessageType() != 3 &&
                aisMessage.getMessageType() != 5;
    }
}
