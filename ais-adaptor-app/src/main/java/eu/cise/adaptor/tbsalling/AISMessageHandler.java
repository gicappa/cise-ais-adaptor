package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.AISProcessor;
import eu.cise.adaptor.DefaultAISProcessor;
import eu.cise.adaptor.DefaultTranslator;
import eu.cise.adaptor.Translator;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;
import jrc.cise.gw.communication.DispatchResult;
import jrc.cise.gw.communication.Dispatcher;

import java.util.function.Consumer;

public class AISMessageHandler implements Consumer<AISMessage> {

    private final DefaultNormalizer normalizer;
    private final Translator translator;
    private final Dispatcher dispatcher;
    private final AISProcessor processor;
    private final XmlMapper mapper;

    public AISMessageHandler() {
        translator = new DefaultTranslator();
        normalizer = new DefaultNormalizer();
        mapper = new DefaultXmlMapper.Pretty();
        dispatcher = (message, address) -> {
            System.out.println(mapper.toXML(message));
            return DispatchResult.success();
        };

        processor = new DefaultAISProcessor(translator, dispatcher);
    }

    @Override
    public void accept(AISMessage aisMessage) {
        processor.process(normalizer.normalize(aisMessage));
    }
}
