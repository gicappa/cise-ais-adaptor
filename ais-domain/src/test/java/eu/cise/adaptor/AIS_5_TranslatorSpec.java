package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AISTranslator;
import eu.cise.adaptor.translate.DefaultAISTranslator;
import eu.cise.datamodel.v1.entity.location.PortLocation;
import eu.cise.datamodel.v1.entity.movement.Movement;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.datamodel.v1.entity.vessel.VesselType;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static eu.cise.adaptor.helpers.Utils.extractVessel;
import static eu.cise.datamodel.v1.entity.movement.MovementType.VOYAGE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class AIS_5_TranslatorSpec {
    {
        describe("an AIS to CISE message translator", () -> {

            AISAdaptorConfig config = ConfigFactory.create(AISAdaptorConfig.class);
            AISTranslator translator = new DefaultAISTranslator(config);

            final AISMsg m = new AISMsg.Builder(5)
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
                    .build();

            describe("when a message type is 5", () -> {
                final Vessel v = extractVessel(translator.translate(m));

                it("returns an Optional<Push> with an ship name", () -> {
                    assertThat(v.getNames(), is(not(empty())));
                    assertThat(v.getNames().get(0), is("QUEEN MARY III"));
                });
                it("returns an Optional<Push> with an ship name", () -> {
                    assertThat(v.getBeam(), is(30));
                });
                it("returns an Optional<Push> with a call sign", () -> {
                    assertThat(v.getCallSign(), is("C1PP4"));
                });
                it("returns an Optional<Push> with a draught", () -> {
                    assertThat(v.getDraught(), is(34.5D));
                });
                it("returns an Optional<Push> with an imo number", () -> {
                    assertThat(v.getIMONumber(), is(123456L));
                });
                it("returns an Optional<Push> with a length", () -> {
                    assertThat(v.getLength(), is(120D)); // A+B
                });
                it("returns an Optional<Push> with an MMSI", () -> {
                    assertThat(v.getMMSI(), is(12345678L));
                });
                it("returns an Optional<Push> with an ship type", () -> {
                    assertThat(v.getShipTypes(), is(not(empty())));
                    assertThat(v.getShipTypes().get(0), is(VesselType.OIL_TANKER));
                });
                it("returns an Optional<Push> with a InvolvedEventRel", () -> {
                    assertThat(v.getInvolvedEventRels(), is(not(empty())));
                });
                context("Involved Event", () -> {
                    final Movement mo = getMovement(extractVessel(translator.translate(m)));
                    it("returns an Optional<Push> with an Movement", () -> {
                        assertThat(mo, instanceOf(Movement.class));
                    });
                    it("returns an Optional<Push> with an MovementType", () -> {
                        assertThat(mo.getMovementType(), is(VOYAGE));
                    });
                    it("returns an Optional<Push> with a LocationRel", () -> {
                        assertThat(mo.getLocationRels(), is(not(empty())));
                    });
                    it("returns an Optional<Push> with a Location", () -> {
                        assertThat(getLocation(mo), instanceOf(PortLocation.class));
                    });
                    it("returns an Optional<Push> with a LocationCode", () -> {
                        assertThat(getLocation(mo).getLocationCode(), is("FRLEH"));
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

