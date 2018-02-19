package eu.cise.adaptor.tbsalling;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import eu.cise.adaptor.Translator;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

import java.util.Optional;
import java.util.function.Consumer;

public class AISMessageHandler implements Consumer<AISMessage> {

    private final Normalizer normalizer = new Normalizer();
    private final Translator translator = new Translator();
    private final XmlMapper mapper = new DefaultXmlMapper.Pretty();

    @Override
    public void accept(AISMessage t) {
        String result = Optional.of(normalizer.normalize(t))
                .flatMap(translator::translate)
                .map(mapper::toXML)
                .orElse("-- Skipping AIS MessageType not supported --\n");

        System.out.println(result);
    }
}
