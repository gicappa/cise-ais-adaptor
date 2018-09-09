package eu.cise.adaptor;

import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.function.Predicate;

import static eu.cise.adaptor.translate.utils.NavigationStatus.UnderwayUsingEngine;
import static org.mockito.Mockito.mock;

public class StreamProcessorIntegrationTest {

    final AisMsg aisMessage = new AisMsg.Builder(1)
            .withLatitude(47.443634F)
            .withLongitude(-6.9895167F)
            .withPositionAccuracy(1)
            .withCOG(2119.0F)
            .withTrueHeading(210)
            .withTimestamp(Instant.parse("2018-02-19T14:43:16.550Z"))
            .withSOG(138.0F)
            .withUserId(538005989)
            .withNavigationStatus(UnderwayUsingEngine)
            .build();

    private StringToAisMsg stringToAisMsg;
    private AisMsgToVessel aisMsgToVessel;
    private VesselToPushMessage vesselToPushMessage;
    private DefaultPipeline processor;

    @Before
    public void before() {
        AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);

        stringToAisMsg = mock(StringToAisMsg.class);
        aisMsgToVessel = new AisMsgToVessel(config);
        vesselToPushMessage = new VesselToPushMessage(config);
        processor = new DefaultPipeline(stringToAisMsg, aisMsgToVessel, vesselToPushMessage, config);
    }

    @Test
    public void it_translate_to_a_push() {
        translateAisMessage(push -> push instanceof Push);
    }

    @Test
    public void it_contains_a_vessel() {
        translateAisMessage(push -> vessel(push) instanceof Vessel);
    }

    @Test
    public void it_contains_a_vessel_with_mmsi() {
        translateAisMessage(push -> vessel(push).getMMSI().equals(538005989l));
    }

    // private helpers
    public void translateAisMessage(Predicate predicate) {
        Flux flux = Flux.just(aisMessage);

        StepVerifier.create(
                processor.toPushMessageFlux(flux))
                .expectNextMatches(predicate)
                .verifyComplete();
    }

    private Vessel vessel(Object push) {
        return (Vessel) ((XmlEntityPayload) ((Push) push).getPayload()).getAnies().get(0);
    }
}

