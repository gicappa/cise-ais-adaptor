package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.datamodel.v1.entity.Entity;
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

            AISAdaptorConfig config = ConfigFactory.create(AISAdaptorConfig.class);
            AisMsgToCiseModel translator = new AisMsgToCiseModel(config);

            describe("when a message type is not supported", () -> {
                asList(4, 6, 7, 8, 9, 10, 11).forEach((n) ->
                        it("returns an empty optional / " + n, () -> {
                            assertThat(translator.translate(new AISMsg.Builder(8).build()), is(Optional.empty()));
                        })
                );
            });

            describe("when a message type is 1,2,3 or 5", () -> {

                asList(1, 2, 3, 5).forEach((n) -> {
                            final AISMsg m = new AISMsg.Builder(n)
                                    .withUserId(538005989)
                                    .build();

                            it("returns an optional with an entity / " + n, () ->
                                    assertThat(translator.translate(m), is(not(Optional.empty()))));

                            it("returns an Optional<Vessel>", () -> {
                                Optional<Entity> entity = translator.translate(m);

                                assertThat("The element in the payload must be a Vessel",
                                        entity.get(), instanceOf(Vessel.class));
                            });
                        }
                );
            });
        });
    }
}

