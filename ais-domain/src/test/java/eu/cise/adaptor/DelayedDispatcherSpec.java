/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.adaptor;


import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.ServiceProfileReader;
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
                    new VesselToPushMessage(config, mock(ServiceProfileReader.class));

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