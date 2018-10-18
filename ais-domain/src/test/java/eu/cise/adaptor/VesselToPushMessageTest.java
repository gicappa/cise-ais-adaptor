package eu.cise.adaptor;

import eu.cise.adaptor.translate.ServiceProfiles;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.servicemodel.v1.message.Push;
import eu.cise.servicemodel.v1.service.ServiceProfile;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VesselToPushMessageTest {

    private VesselToPushMessage vesselToPush;
    private ServiceProfiles profiles;

    @Before
    public void before() {
        AdaptorConfig config = ConfigFactory.create(AdaptorConfig.class);
        profiles = mock(ServiceProfiles.class);

        vesselToPush = new VesselToPushMessage(config, profiles);
    }

    @Test
    public void it_translate_a_list_of_vessel_to_a_push() {
        when(profiles.list()).thenReturn(asList(new ServiceProfile(), new ServiceProfile()));

        Push actual = vesselToPush.translate(singletonList(new Vessel()));

        assertThat(actual.getDiscoveryProfiles(), hasSize(2));
    }
}