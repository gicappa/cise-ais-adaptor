package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.normalize.AISNormalizer;

import java.util.Optional;

public class DefaultAISMsgNormalizer implements AISNormalizer {

    private final NMEAMessageTranslator nt;
    private final SimpleNMEAWhatever simpleNMEAWhatever;
    private final TBSAISNormalizer tn;

    public DefaultAISMsgNormalizer(
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
                .flatMap(aisMessage -> aisMessage.map(a -> tn.translate(a)));
    }
}
