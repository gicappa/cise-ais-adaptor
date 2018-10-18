package eu.cise.adaptor;

import com.greghaskins.spectrum.Spectrum;
import eu.cise.adaptor.translate.ServiceProfiles;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.XmlEntityPayload;
import org.aeonbits.owner.ConfigFactory;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static eu.cise.adaptor.helpers.Utils.extractPayload;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(Spectrum.class)
public class ServiceTranslatorSpec {
    {
        describe("the cise service model added to the entity", () -> {

                     AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);
                     VesselToPushMessage translator =
                             new VesselToPushMessage(config, mock(ServiceProfiles.class));

                     Vessel vessel = new Vessel();

                     it("returns an optional with a push message", () ->
                             assertThat(translator.translate(singletonList(vessel)),
                                        is(not(Optional.empty()))));

                     it("returns an Optional<Push> with an XmlEntityPayload", () -> {
                         XmlEntityPayload payload
                                 = extractPayload(translator.translate(singletonList(vessel)));

                         assertThat("The XmlEntityPayload has not been created",
                                    payload, is(notNullValue()));
                     });

                     it("returns an Optional<Push> with a vessel", () -> {
                         XmlEntityPayload payload
                                 = extractPayload(translator.translate(singletonList(vessel)));
                         List<Object> vessels = payload.getAnies();
                         assertThat("There must be at least one vessel element i the payload",
                                    vessels, is(not(empty())));

                         assertThat("The element in the payload must be a Vessel",
                                    vessels.get(0), instanceOf(Vessel.class));
                     });
                 }
                );
    }
}

