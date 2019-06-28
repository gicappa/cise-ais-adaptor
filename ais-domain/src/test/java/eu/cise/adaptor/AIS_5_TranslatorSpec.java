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
import eu.cise.adaptor.translate.Message5Translator;
import eu.cise.datamodel.v1.entity.location.PortLocation;
import eu.cise.datamodel.v1.entity.movement.Movement;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.datamodel.v1.entity.vessel.VesselType;
import eu.eucise.xml.DefaultXmlMapper;
import org.junit.runner.RunWith;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;

import static com.greghaskins.spectrum.Spectrum.*;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static eu.cise.adaptor.heplers.Utils.xmlDate;
import static eu.cise.adaptor.heplers.Utils.xmlTime;
import static eu.cise.datamodel.v1.entity.movement.MovementType.VOYAGE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@SuppressWarnings("all")
@RunWith(Spectrum.class)
public class AIS_5_TranslatorSpec {
    {
        describe("an AIS to CISE message translator", () -> {

            Message5Translator translator = new Message5Translator();

            final AisMsg m = new AisMsg.Builder(5)
                    .withUserId(12345678)
                    .withShipName("QUEEN MARY III")
                    .withDimensionA(100)
                    .withDimensionB(20)
                    .withDimensionC(10)
                    .withDimensionD(20)
                    .withCallSign("C1PP4")
                    .withDraught(34.5F)
                    .withIMONumber(123456)
                    .withShipType(84)
                    .withDestination("FRLEH")
                    .withEta(Instant.parse("2019-06-19T15:43:00Z"))
                    .build();

            describe("when a message type is 5", () -> {
                final Vessel v = translator.translate(m);

                it("returns a Vessel with an ship name", () -> {
                    assertThat(v.getNames(), is(not(empty())));
                    assertThat(v.getNames().get(0), is("QUEEN MARY III"));
                });
                it("returns a Vessel with an ship name", () -> {
                    assertThat(v.getBeam(), is(30));
                });
                it("returns a Vessel with a call sign", () -> {
                    assertThat(v.getCallSign(), is("C1PP4"));
                });
                it("returns a Vessel with a draught", () -> {
                    assertThat(v.getDraught(), is(34.5D));
                });
                it("returns a Vessel with an imo number", () -> {
                    assertThat(v.getIMONumber(), is(123456L));
                });
                it("returns a Vessel with a length", () -> {
                    assertThat(v.getLength(), is(120D)); // A+B
                });
                it("returns a Vessel with an MMSI", () -> {
                    assertThat(v.getMMSI(), is(12345678L));
                });
                it("returns a Vessel with an ship type", () -> {
                    assertThat(v.getShipTypes(), is(not(empty())));
                    assertThat(v.getShipTypes().get(0), is(VesselType.OIL_TANKER));
                });
                it("returns a Vessel with a InvolvedEventRel", () -> {
                    assertThat(v.getInvolvedEventRels(), is(not(empty())));
                });

                context("Involved Event with a location code", () -> {
                    final Movement mo = getMovement(translator.translate(m));
                    it("returns a Vessel with an Movement", () -> {
                        assertThat(mo, instanceOf(Movement.class));
                    });
                    it("returns a Vessel with an MovementType", () -> {
                        assertThat(mo.getMovementType(), is(VOYAGE));
                    });
                    it("returns a Vessel with a LocationRel", () -> {
                        assertThat(mo.getLocationRels(), is(not(empty())));
                    });
                    it("returns a Vessel with an ETA date", () -> {
                        assertThat(mo.getLocationRels().get(0).getDateTime().getStartDate(), is(xmlDate(Instant.parse("2019-06-19T00:00:00Z"))));
                    });
                    it("returns a Vessel with an ETA time", () -> {
                        assertThat(mo.getLocationRels().get(0).getDateTime().getStartTime(), is(xmlTime(Instant.parse("1970-01-01T15:43:00Z"))));
                    });
                    it("returns a Vessel with a Location", () -> {
                        assertThat(getLocation(mo), instanceOf(PortLocation.class));
                    });
                    it("returns a Vessel with a LocationCode", () -> {
                        assertThat(getLocation(mo).getLocationCode(), is("FRLEH"));
                    });
                    fit("returns a Vessel with a PortName", () -> {
                        System.out.print(new DefaultXmlMapper.Pretty().toXML(v));
                        assertThat(getLocation(mo).getPortName(), is("FRLEH"));

                    });
                });
                context("Involved Event with a port name", () -> {
                    final AisMsg m1 = new AisMsg.Builder(5)
                            .withUserId(12345678)
                            .withShipName("QUEEN MARY III")
                            .withDimensionA(100)
                            .withDimensionB(20)
                            .withDimensionC(10)
                            .withDimensionD(20)
                            .withCallSign("C1PP4")
                            .withDraught(34.5F)
                            .withIMONumber(123456)
                            .withShipType(84)
                            .withDestination("Le Havre")
                            .build();

                    final Movement mo = getMovement(translator.translate(m1));

                    it("returns a Vessel with a LocationCode", () -> {
                        assertThat(getLocation(mo).getLocationCode(), is(nullValue()));
                    });
                    it("returns a Vessel with a PortName", () -> {
                        assertThat(getLocation(mo).getPortName(), is("Le Havre"));
                    });
                });

            });
        });
    }

    private PortLocation getLocation(Movement m) {
        return (PortLocation) m.getLocationRels().get(0).getLocation();
    }

    private Movement getMovement(Vessel v) {
        return (Movement) v.getInvolvedEventRels().get(0).getEvent();
    }

}

