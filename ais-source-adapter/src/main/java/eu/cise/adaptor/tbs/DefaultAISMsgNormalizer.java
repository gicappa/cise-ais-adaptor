package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISMsg;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.datamodel.v1.entity.Entity;
import reactor.core.publisher.Flux;

public class DefaultAISMsgNormalizer implements AISNormalizer {
    @Override
    public Flux<AISMsg> translate(Flux<String> type) {
        throw new RuntimeException("not implemented");
    }

//    private final NMEAMessageTranslator nt;
//    private final SimpleNMEAWhatever simpleNMEAWhatever;
//    private final TBSAISNormalizer tn;
//
//    public DefaultAISMsgNormalizer(
//            NMEAMessageTranslator nt,
//            SimpleNMEAWhatever simpleNMEAWhatever,
//            TBSAISNormalizer tn) {
//        this.nt = nt;
//        this.simpleNMEAWhatever = simpleNMEAWhatever;
//        this.tn = tn;
//    }
//
//    @Override
//    public Optional<AISMsg> process(String type) {
//        return nt.process(type)
//                .map(nmea -> simpleNMEAWhatever.process(nmea))
//                .flatMap(aisMessage -> aisMessage.map(a -> tn.process(a)));
//    }
}
