package eu.cise.adaptor;

import eu.cise.servicemodel.v1.message.Push;

import java.util.Optional;

public class ToCISETranslator {
    public Optional<Push> translate(InternalAISMessage aisMessage) {
        if (isTypeSupported(aisMessage)) {
            return Optional.empty();
        }
        return Optional.of(new Push());
    }

    private boolean isTypeSupported(InternalAISMessage aisMessage) {
        return aisMessage.getMessageType() !=1 &&
                aisMessage.getMessageType() != 2 &&
                aisMessage.getMessageType() != 3 &&
                aisMessage.getMessageType() != 5;
    }
}
