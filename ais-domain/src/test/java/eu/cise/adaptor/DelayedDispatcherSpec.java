package eu.cise.adaptor;


import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.ServiceProfiles;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Message;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Spectrum.class)
public class DelayedDispatcherSpec {
    {
        describe("aggregates a stream of messages", () -> {

            Vessel vessel = new Vessel();
            vessel.setMMSI(12345L);

            StringToAisMsg stringToAisMsg = mock(StringToAisMsg.class);
            AisMsgToVessel aisMsgToVessel = mock(AisMsgToVessel.class);
            AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);
            VesselToPushMessage vesselToPushMessage =
                    new VesselToPushMessage(config, mock(ServiceProfiles.class));

            when(aisMsgToVessel.translate(any())).thenReturn(Optional.of(vessel));


            AisMsg aisMsg = new AisMsg.Builder(1).build();

            it("does't dispatch a single message ", () -> {
                DefaultPipeline pipeline = new DefaultPipeline(stringToAisMsg,
                                                               aisMsgToVessel,
                                                               vesselToPushMessage,
                                                               config);
                Flux<AisMsg> flux = Flux.just(aisMsg);

                StepVerifier.create(pipeline.toPushMessageFlux(flux))
                        .assertNext(m -> assertThat(vesselList(m), hasSize(1)))
                        .expectComplete()
                        .verify();
            });

            it("accumulate two messages and it dispatches the third", () -> {
                DefaultPipeline pipeline = new DefaultPipeline(stringToAisMsg,
                                                               aisMsgToVessel,
                                                               vesselToPushMessage,
                                                               config);

                Flux<AisMsg> flux = Flux.just(aisMsg, aisMsg, aisMsg, aisMsg);

                StepVerifier.create(pipeline.toPushMessageFlux(flux))
                        .assertNext(m -> assertThat(vesselList(m), hasSize(3)))
                        .assertNext(m -> assertThat(vesselList(m), hasSize(1)))
                        .expectComplete()
                        .verify();
            });

        });
    }

    public static List<Vessel> vesselList(Message push) {
        return ((XmlEntityPayload) push.getPayload()).getAnies().stream().map(Vessel.class::cast).collect(toList());
    }

}