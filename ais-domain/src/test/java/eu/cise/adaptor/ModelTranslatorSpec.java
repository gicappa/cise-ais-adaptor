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
import eu.cise.datamodel.v1.entity.Entity;
import eu.cise.datamodel.v1.entity.uniqueidentifier.UniqueIdentifier;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class ModelTranslatorSpec {
    {
        describe("an AIS to CISE model translator", () -> {

            AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);
            AisMsgToVessel translator = new AisMsgToVessel(config);

            describe("when a message type is not supported", () -> {
                asList(4, 6, 7, 8, 9, 10, 11)
                        .forEach((n) ->
                                         it("returns an empty optional / " + n, () -> {
                                             assertThat(translator.translate(new AisMsg.Builder(8).build()), is(Optional.empty()));
                                         })
                                );
            });

            describe("when a message type is 1,2,3 or 5", () -> {

                asList(1, 2, 3, 5).forEach((n) -> {
                    final AisMsg m = new AisMsg.Builder(n)
                            .withUserId(538005989)
                            .build();

                    it("returns an optional with an entity / " + n, () ->
                            assertThat(translator.translate(m), is(not(Optional.empty()))));

                    it("returns an Optional<Vessel> / " + n, () -> {
                        Optional<Entity> entity = translator.translate(m);

                        assertThat("The element in the payload must be a Vessel",
                                   entity.get(), instanceOf(Vessel.class));
                    });

                    it("returns a vessel with a uuid / " + n, () -> {
                        Vessel vessel = ((Vessel) translator.translate(m).get());

                        assertThat("the uuid is not null",
                                   vessel.getIdentifier(), notNullValue());
                    });

                    it("returns a uuid with an organization / " + n, () -> {
                        UniqueIdentifier id
                                = ((Vessel) translator.translate(m).get()).getIdentifier();

                        assertThat("the org is not null", id.getGeneratedBy(), notNullValue());
                    });
                });
            });
        });
    }
}

