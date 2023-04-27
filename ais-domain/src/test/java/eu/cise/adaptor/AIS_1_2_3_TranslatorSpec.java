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
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static eu.cise.adaptor.heplers.Utils.extractGeometry;
import static eu.cise.adaptor.heplers.Utils.extractLocation;
import static eu.cise.adaptor.heplers.Utils.extractLocationRel;
import static eu.cise.adaptor.heplers.Utils.xmlDate;
import static eu.cise.adaptor.heplers.Utils.xmlTime;
import static eu.cise.adaptor.translate.utils.NavigationStatus.UnderwayUsingEngine;
import static eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType.HIGH;
import static eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType.MEDIUM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.Message123Translator;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import java.time.Instant;
import org.junit.runner.RunWith;

@SuppressWarnings("all")
@RunWith(Spectrum.class)
public class AIS_1_2_3_TranslatorSpec {

    {
        describe("an AIS to CISE message translator", () -> {

            AdaptorConfig config = mock(AdaptorConfig.class);
            Message123Translator translator = new Message123Translator(config);

            final AisMsg m = new AisMsg.Builder(1)
                .withLatitude(47.443634F)
                .withLongitude(-6.9895167F)
                .withPositionAccuracy(1)
                .withCOG(211.9F)
                .withTrueHeading(210)
                .withTimestamp(Instant.parse("2018-02-19T14:43:16.550Z"))
                .withSOG(13.8F)
                .withUserId(538005989)
                .withNavigationStatus(UnderwayUsingEngine)
                .build();

            describe("when a message type is 1,2,3", () -> {
                final Vessel v = translator.translate(m);

                context("when returns a Vessel with a geometry", () -> {

                    it("the geometry elements are not null and well formed", () -> {
                        assertThat(v.getLocationRels()).isNotEmpty();
                        assertThat(extractLocationRel(v).getLocation()).isNotNull();
                        assertThat(extractLocationRel(v).getLocation().getGeometries()).isNotEmpty();
                        assertThat(extractLocationRel(v).getLocation().getGeometries().get(0)).isNotNull();
                    });

                    it("the latitude is extracted correctly", () -> {
                        assertThat(extractGeometry(v).getLatitude()).isEqualTo("47.443634");
                    });

                    it("the longitude is extracted correctly", () -> {
                        assertThat(extractGeometry(v).getLongitude()).isEqualTo("-6.9895167");
                    });

                    context("when the config deleteIncorrectGeoLocation is true", () -> {
                        beforeAll(() -> {
                            when(config.deleteLocationUnavailable()).thenReturn(true);
                        });

                        it("the latitude is null when ais defaults to 91", () -> {
                            AisMsg m91 = new AisMsg.Builder(1)
                                .withLatitude(91F).withLongitude(-6.9895167F).build();
                            Vessel v91 = translator.translate(m91);

                            assertThat(extractLocation(v91)).isNull();
                        });

                        it("the longitude is null when ais defaults to 181", () -> {
                            AisMsg m181 = new AisMsg.Builder(1)
                                .withLatitude(47.443634F).withLongitude(181F).build();
                            Vessel v181 = translator.translate(m181);

                            assertThat(extractLocation(v181)).isNull();
                        });
                    });

                    context("when the config deleteLocationIfNotProvided is false", () -> {
                        beforeAll(() -> {
                            when(config.deleteLocationUnavailable()).thenReturn(false);
                        });

                        it("the latitude is 91 when ais defaults to 91", () -> {
                            AisMsg m91 = new AisMsg.Builder(1)
                                .withLatitude(91F).withLongitude(-6.9895167F).build();
                            Vessel v91 = translator.translate(m91);

                            assertThat(extractGeometry(v91).getLatitude()).isEqualTo("91.0");
                        });

                        it("the longitude is 181 when ais defaults to 181", () -> {
                            AisMsg m181 = new AisMsg.Builder(1)
                                .withLatitude(47.443634F).withLongitude(181F).build();
                            Vessel v181 = translator.translate(m181);

                            assertThat(extractGeometry(v181).getLongitude()).isEqualTo("181.0");
                        });
                    });
                });

                context("returns a Vessel with location qualitative accuracy", () -> {

                    it("HIGH for AIS position accuracy 1", () -> {
                        assertThat(extractLocationRel(v).getLocation()
                            .getLocationQualitativeAccuracy()).isEqualTo(HIGH);
                    });

                    it("MEDIUM for AIS position accuracy 0", () -> {
                        AisMsg mMedium = new AisMsg.Builder(1).withPositionAccuracy(0).build();
                        Vessel vMedium = translator.translate(mMedium);

                        assertThat(extractLocationRel(vMedium).getLocation()
                            .getLocationQualitativeAccuracy()).isEqualTo(MEDIUM);
                    });
                });

                it("returns a Vessel with cog (in degrees instead of 1/10 od degrees)", () -> {
                    assertThat(extractLocationRel(v).getCOG()).isEqualTo(211.9D);
                });

                it("returns a Vessel with cog (null for cog=3600)", () -> {
                    AisMsg mc = new AisMsg.Builder(1)
                        .withCOG(360f)
                        .build();

                    Vessel vc = translator.translate(mc);

                    assertThat(extractLocationRel(vc).getCOG()).isNull();
                });

                it("returns a Vessel with true heading", () -> {
                    assertThat(extractLocationRel(v).getHeading()).isEqualTo(210D);
                });

                it("returns a Vessel with heading (null for trueHeading=511)", () -> {
                    AisMsg mh = new AisMsg.Builder(1)
                        .withTrueHeading(511)
                        .build();

                    Vessel vh = translator.translate(mh);

                    assertThat(extractLocationRel(vh).getHeading()).isNull();
                });

                it("returns a Vessel with source type", () -> {
                    assertThat(extractLocationRel(v).getSourceType()).isEqualTo(SourceType.DECLARATION);
                });

                it("returns a Vessel with sensor type", () -> {
                    assertThat(extractLocationRel(v).getSensorType()).isEqualTo(SensorType.AUTOMATIC_IDENTIFICATION_SYSTEM);
                });

                it("returns a Vessel with sog (in knots instead of 1/10th of knots)", () -> {
                    assertThat(extractLocationRel(v).getSOG()).isEqualTo(13.8D);
                });

                it("returns a Vessel with sog (null for SOG=1023)", () -> {
                    AisMsg ms = new AisMsg.Builder(1)
                        .withSOG(102.3F)
                        .build();

                    Vessel vs = translator.translate(ms);
                    assertThat(extractLocationRel(vs).getSOG()).isNull();
                });

                it("returns a Vessel with periodOfTime.startDate", () -> {
                    //"2018-02-19T14:43:16.550Z"
                    assertThat(extractLocationRel(v).getPeriodOfTime().getStartDate())
                        .isEqualTo(xmlDate(2018, 2, 19));
                });

                it("returns a Vessel with periodOfTime.startTime", () -> {
                    //"2018-02-19T14:43:16.550Z"
                    assertThat(extractLocationRel(v).getPeriodOfTime().getStartTime())
                        .isEqualTo(xmlTime(14, 43, 16));
                });

                it("returns a Vessel with MMSI", () -> {
                    assertThat(v.getMMSI()).isEqualTo(538005989L);
                });

                it("returns a Vessel with navigationStatus", () -> {
                    assertThat(v.getNavigationalStatus())
                        .isEqualTo(NavigationalStatusType.UNDER_WAY_USING_ENGINE);
                });
            });

        });
    }
}