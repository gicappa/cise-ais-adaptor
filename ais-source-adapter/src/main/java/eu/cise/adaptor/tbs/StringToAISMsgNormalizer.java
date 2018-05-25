package eu.cise.adaptor.tbs;

import dk.tbsalling.aismessages.nmea.NMEAMessageHandler;
import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.translate.Translator;

import java.util.Optional;

public class StringToAISMsgNormalizer implements Translator<String, Optional<AISMsg>> {

    private final NMEAMessageTranslator nt;
    private final SimpleNMEAWhatever simpleNMEAWhatever;
    private final TBSAISNormalizer tn;

    public StringToAISMsgNormalizer(
            NMEAMessageTranslator nt,
            SimpleNMEAWhatever simpleNMEAWhatever,
            TBSAISNormalizer tn) {
        this.nt = nt;
        this.simpleNMEAWhatever = simpleNMEAWhatever;
        this.tn = tn;
    }

    @Override
    public Optional<AISMsg> translate(String type) {
        return nt.translate(type)
                .map(nmea -> simpleNMEAWhatever.translate(nmea))
                .flatMap(aisMessage -> aisMessage.map(a ->  tn.normalize(a)));
    }
}
