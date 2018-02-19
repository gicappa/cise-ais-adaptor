package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.*;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

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
        dispatcher = message -> {
            System.out.println(mapper.toXML(message));
            return Result.SUCCESS;
        };

        processor = new DefaultAISProcessor(translator, dispatcher);
    }

    @Override
    public void accept(AISMessage aisMessage) {
        processor.process(normalizer.normalize(aisMessage));
    }
}
