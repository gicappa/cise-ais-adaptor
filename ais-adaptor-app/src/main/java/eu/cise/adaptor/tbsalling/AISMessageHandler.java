package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.*;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;
import jrc.cise.gw.communication.DispatchResult;
import jrc.cise.gw.communication.Dispatcher;
import org.aeonbits.owner.ConfigFactory;

import java.util.function.Consumer;

public class AISMessageHandler implements Consumer<AISMessage> {

    private final DefaultAISNormalizer normalizer;
    private final Translator translator;
    private final Dispatcher dispatcher;
    private final AISProcessor processor;
    private final XmlMapper mapper;
    private final AISAdaptorConfig config;

    public AISMessageHandler() {

        config = ConfigFactory.create(AISAdaptorConfig.class);
        translator = new DefaultTranslator();
        normalizer = new DefaultAISNormalizer();
        mapper = new DefaultXmlMapper.Pretty();
        dispatcher = (message, address) -> {
            System.out.println(mapper.toXML(message));
            return DispatchResult.success();
        };

        processor = new DefaultAISProcessor(translator, dispatcher, config);
    }

    @Override
    public void accept(AISMessage aisMessage) {
        processor.process(normalizer.normalize(aisMessage));
    }
}
