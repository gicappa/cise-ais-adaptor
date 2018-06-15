package eu.cise.adaptor;

import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.adaptor.translate.CiseModelToCiseMessage;
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
    private StringToAisMsg aisNormalizer;
    private AisMsgToCiseModel aisMsgToCiseModel;
    private CiseModelToCiseMessage ciseModelToCiseMessage;
    private AisStreamProcessor processor;

    @Before
    public void before() {

        AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);
        aisNormalizer = mock(StringToAisMsg.class);
        aisMsgToCiseModel = new AisMsgToCiseModel(config);
        ciseModelToCiseMessage = new CiseModelToCiseMessage(config);
        processor = new AisStreamProcessor(aisNormalizer, aisMsgToCiseModel, ciseModelToCiseMessage, config);
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
    private Vessel vessel(Object push) {
        return (Vessel) ((XmlEntityPayload) ((Push) push).getPayload()).getAnies().get(0);
    }

    public void translateAisMessage(Predicate predicate) {
        Flux flux = Flux.just(aisMessage);

        StepVerifier.create(
                processor.toCiseMessageFlux(flux))
                .expectNextMatches(predicate)
                .verifyComplete();
    }
}

