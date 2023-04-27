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
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class ModelTranslatorSpec {

    {
        describe("an AIS to CISE model translator", () -> {

            var config = ConfigFactory.create(AdaptorConfig.class);
            var translator = new AisMsgToVessel(config);

            describe("when a message type is not supported", () -> {
                asList(4, 6, 7, 8, 9, 10, 11)
                    .forEach((n) ->
                        it("returns an empty optional / " + n, () -> {
                            assertThat(translator.translate(new AisMsg.Builder(8).build()))
                                .isEmpty();
                        })
                    );
            });

            describe("when a message type is 1,2,3 or 5", () -> {

                asList(1, 2, 3, 5).forEach((n) -> {
                    final var m = new AisMsg.Builder(n)
                        .withUserId(538005989)
                        .build();

                    it("returns an optional with an entity / " + n, () ->
                        assertThat(translator.translate(m)).isNotEmpty());

                    it("returns an Optional<Vessel> / " + n, () -> {
                        var entity = translator.translate(m);

                        assertThat(entity.get()).isInstanceOf(Vessel.class)
                            .describedAs("The element in the payload must be a Vessel");
                    });

                    it("returns a vessel with a uuid / " + n, () -> {
                        var vessel = ((Vessel) translator.translate(m).get());

                        assertThat(vessel.getIdentifier()).isNotNull()
                            .describedAs("the uuid is not null");
                    });

                    it("returns a uuid with an organization / " + n, () -> {
                        var id
                            = ((Vessel) translator.translate(m).get()).getIdentifier();

                        assertThat(id.getGeneratedBy()).isNotNull()
                            .describedAs("the org is not null");
                    });

                    it("returns a legalName in the org object / " + n, () -> {
                        var org = ((Vessel) translator.translate(m).get())
                            .getIdentifier().getGeneratedBy();

                        assertThat(org.getLegalName()).isNotNull()
                            .describedAs("the legalName is not null");

                        assertThat(org.getLegalName()).isEqualTo(config.getOrgLegalName())
                            .describedAs("the legalName is the one specified in the config");
                    });

                    it("returns a alternativeName in the org object / " + n, () -> {
                        var org = ((Vessel) translator.translate(m).get())
                            .getIdentifier().getGeneratedBy();

                        assertThat(org.getAlternativeName()).isNotNull()
                            .describedAs("the legalName is not null");

                        assertThat(org.getAlternativeName())
                            .isEqualTo(config.getOrgAlternativeName())
                            .describedAs("the legalName is the one specified in the config");
                    });
                    it("returns an uuid string not null / " + n, () -> {
                        var uuid = ((Vessel) translator.translate(m)
                            .get()).getIdentifier();

                        System.out.println(uuid.getUUID());
                        assertThat(uuid.getUUID()).isNotNull()
                            .describedAs("the uuid is not null");
                    });
                });
            });
        });
    }
}

