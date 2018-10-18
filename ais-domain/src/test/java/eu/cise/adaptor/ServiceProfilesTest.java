package eu.cise.adaptor;

import eu.cise.adaptor.translate.ServiceProfiles;
import eu.cise.servicemodel.v1.service.ServiceProfile;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static eu.cise.servicemodel.v1.authority.CommunityType.CUSTOMS;
import static eu.cise.servicemodel.v1.authority.CountryType.ES;
import static eu.cise.servicemodel.v1.authority.FunctionType.CUSTOMS_MONITORING;
import static eu.cise.servicemodel.v1.authority.SeaBasinType.ARCTIC_OCEAN;
import static eu.cise.servicemodel.v1.service.DataFreshnessType.NEARLY_REAL_TIME;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * profile.0.service_id=id1
 * profile.0.community=Customs
 * profile.0.country=ES
 * profile.0.data_freshness=NearlyRealTime
 * profile.0.function=CustomsMonitoring
 * profile.0.sea_basin=ArcticOcean
 */
public class ServiceProfilesTest {

    private List<ServiceProfile> actual;

    @Before
    public void before() {
        ServiceProfiles profiles = new ServiceProfiles();
        actual = profiles.list();
    }

    @Test
    public void it_reads_a_service_profile_from_property_file() {
        assertThat(actual, hasSize(2));
    }

    @Test
    public void it_reads_a_service_id() {
        assertThat(actual.get(0).getServiceID(), is("id1"));
    }

    @Test
    public void it_reads_a_community() {
        assertThat(actual.get(0).getCommunity(), is(CUSTOMS));
    }

    @Test
    public void it_reads_a_country() {
        assertThat(actual.get(0).getCountry(), is(ES));
    }

    @Test
    public void it_reads_data_freshness() {
        assertThat(actual.get(0).getDataFreshness(), is(NEARLY_REAL_TIME));
    }

    @Test
    public void it_reads_data_functions() {
        assertThat(actual.get(0).getFunction(), is(CUSTOMS_MONITORING));
    }

    @Test
    public void it_reads_sea_basin() {
        assertThat(actual.get(0).getSeaBasin(), is(ARCTIC_OCEAN));
    }
}
