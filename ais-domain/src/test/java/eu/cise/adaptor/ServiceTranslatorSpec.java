/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
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

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static eu.cise.adaptor.heplers.Utils.extractPayload;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.ServiceProfileReader;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import java.util.List;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class ServiceTranslatorSpec {

    {
        describe("the cise service model added to the entity", () -> {

            var config = ConfigFactory.create(AdaptorConfig.class);
            var translator =
                new VesselToPushMessage(config, mock(ServiceProfileReader.class));

            var vessel = new Vessel();

            it("returns an optional with a push message", () ->
                assertThat(translator.translate(singletonList(vessel)))
                    .isInstanceOf(Push.class));

            it("returns an Optional<Push> with an XmlEntityPayload", () -> {
                var payload
                    = extractPayload(translator.translate(singletonList(vessel)));

                assertThat(payload)
                    .describedAs("The XmlEntityPayload has not been created")
                    .isNotNull();
            });

            it("returns an Optional<Push> with a vessel", () -> {
                XmlEntityPayload payload
                    = extractPayload(translator.translate(singletonList(vessel)));
                List<Object> vessels = payload.getAnies();
                assertThat(vessels)
                    .describedAs("There must be at least one vessel element i the payload")
                    .isNotEmpty();

                assertThat(vessels.get(0))
                    .isInstanceOf(Vessel.class)
                    .describedAs("The element in the payload must be a Vessel");
            });
        });
    }
}

