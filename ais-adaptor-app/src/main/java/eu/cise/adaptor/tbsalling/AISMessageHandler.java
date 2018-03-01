package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.*;
import jrc.cise.gw.sending.Dispatcher;
import jrc.cise.transport.RestDispatcher;
import org.aeonbits.owner.ConfigFactory;

import java.util.function.Consumer;

public class AISMessageHandler implements Consumer<AISMessage> {

    private final DefaultAISNormalizer normalizer;
    private final Translator translator;
    private final Dispatcher dispatcher;
    private final AISProcessor processor;
    private final AISAdaptorConfig config;

    public AISMessageHandler() {

        config = ConfigFactory.create(AISAdaptorConfig.class);
        translator = new DefaultTranslator(config);
        normalizer = new DefaultAISNormalizer();
        dispatcher = new RestDispatcher();
        processor = new DefaultAISProcessor(translator, dispatcher, config);
    }

    @Override
    public void accept(AISMessage aisMessage) {
        processor.process(normalizer.normalize(aisMessage));
    }
}
