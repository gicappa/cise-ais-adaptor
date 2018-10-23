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

import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.ServiceProfileReader;
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

    private final AisMsg aisMessage = new AisMsg.Builder(1)
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

    private DefaultPipeline processor;

    @Before
    public void before() {
        AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);

        StringToAisMsg stringToAisMsg = mock(StringToAisMsg.class);
        AisMsgToVessel aisMsgToVessel = new AisMsgToVessel(config);
        VesselToPushMessage vesselToPushMessage =
                new VesselToPushMessage(config, mock(ServiceProfileReader.class));
        processor
                = new DefaultPipeline(stringToAisMsg, aisMsgToVessel, vesselToPushMessage, config);
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
    private void translateAisMessage(Predicate predicate) {
        Flux<AisMsg> flux = Flux.just(aisMessage);

        StepVerifier.create(
                processor.toPushMessageFlux(flux))
                .expectNextMatches(predicate)
                .verifyComplete();
    }

    private Vessel vessel(Object push) {
        return (Vessel) ((XmlEntityPayload) ((Push) push).getPayload()).getAnies().get(0);
    }
}

