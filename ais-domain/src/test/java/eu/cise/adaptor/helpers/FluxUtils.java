package eu.cise.adaptor.helpers;

import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;

public class FluxUtils {

//    public static void translateAisMessage(Predicate predicate) {
//
//        Flux flux = Flux.just();
//
//        StepVerifier.create(
//                toPushMessageFlux(flux))
//                .expectNextMatches(predicate)
//                .verifyComplete();
//    }

    public static Vessel vessel(Object push) {
        return (Vessel) ((XmlEntityPayload) ((Push) push).getPayload()).getAnies().get(0);
    }
}
