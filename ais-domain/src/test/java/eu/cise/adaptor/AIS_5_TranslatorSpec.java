package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AISTranslator;
import eu.cise.adaptor.translate.DefaultAISTranslator;
import eu.cise.datamodel.v1.entity.event.Event;
import eu.cise.datamodel.v1.entity.location.Geometry;
import eu.cise.datamodel.v1.entity.location.Location;
import eu.cise.datamodel.v1.entity.location.LocationZoneType;
import eu.cise.datamodel.v1.entity.location.PortLocation;
import eu.cise.datamodel.v1.entity.movement.Movement;
import eu.cise.datamodel.v1.entity.movement.MovementType;
import eu.cise.datamodel.v1.entity.object.Objet;
import eu.cise.datamodel.v1.entity.object.SensorType;
import eu.cise.datamodel.v1.entity.object.SourceType;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.xdescribe;
import static com.greghaskins.spectrum.dsl.specification.Specification.xit;
import static eu.cise.adaptor.helpers.Utils.extractLocationRel;
import static eu.cise.adaptor.helpers.Utils.extractVessel;
import static eu.cise.adaptor.normalize.NavigationStatus.UnderwayUsingEngine;
import static eu.cise.datamodel.v1.entity.location.LocationQualitativeAccuracyType.HIGH;
import static eu.cise.datamodel.v1.entity.movement.MovementType.VOYAGE;
import static java.util.Arrays.asList;
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
                    .build();

            describe("when a message type is 5", () -> {
                final Vessel v = extractVessel(translator.translate(m));

                it("returns an Optional<Push> with a InvolvedEventRel", () -> {
                    assertThat(v.getInvolvedEventRels(), is(not(empty())));
                });

                it("returns an Optional<Push> with an Movement", () -> {
                    assertThat(getMovement(v), instanceOf(Movement.class));
                });

                it("returns an Optional<Push> with an MovementType", () -> {
                    assertThat(getMovement(v).getMovementType(), is(VOYAGE));
                });

                it("returns an Optional<Push> with a LocationRel", () -> {
                    assertThat(getMovement(v).getLocationRels(), is(not(empty())));
                });

                it("returns an Optional<Push> with a Location", () -> {
                    assertThat(getLocation(v), instanceOf(PortLocation.class));
                });

                xit("returns an Optional<Push> with a LocationCode", () -> {
                    assertThat(getLocation(v).getLocationCode(), is("FRLEH"));
                });

                it("returns an Optional<Push> with an MMSI", () -> {
                    assertThat(v.getMMSI(), is(12345678L));
                });
            });

        });
    }

    private PortLocation getLocation(Vessel v) {
        return (PortLocation) getMovement(v).getLocationRels().get(0).getLocation();
    }

    private Movement getMovement(Vessel v) {
        return (Movement)v.getInvolvedEventRels().get(0).getEvent();
    }

}

