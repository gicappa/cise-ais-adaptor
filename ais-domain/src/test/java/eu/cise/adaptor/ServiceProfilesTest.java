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

import eu.cise.adaptor.translate.ServiceProfileReader;
import eu.cise.servicemodel.v1.service.ServiceProfile;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static eu.cise.servicemodel.v1.authority.CommunityType.CUSTOMS;
import static eu.cise.servicemodel.v1.authority.CountryType.ES;
import static eu.cise.servicemodel.v1.authority.FunctionType.CUSTOMS_MONITORING;
import static eu.cise.servicemodel.v1.authority.SeaBasinType.ARCTIC_OCEAN;
import static eu.cise.servicemodel.v1.service.DataFreshnessType.NEARLY_REAL_TIME;
import static eu.cise.servicemodel.v1.service.ServiceOperationType.PULL;
import static eu.cise.servicemodel.v1.service.ServiceOperationType.PUSH;
import static eu.cise.servicemodel.v1.service.ServiceRoleType.CONSUMER;
import static eu.cise.servicemodel.v1.service.ServiceRoleType.PROVIDER;
import static eu.cise.servicemodel.v1.service.ServiceType.CARGO_SERVICE;
import static eu.cise.servicemodel.v1.service.ServiceType.VESSEL_SERVICE;
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
        ServiceProfileReader profiles = new ServiceProfileReader();
        actual = profiles.list();
    }

    @Test
    public void it_reads_a_service_profile_from_property_file() {
        assertThat(actual, hasSize(3));
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

    @Test
    public void it_reads_default_service_operation() {
        assertThat(actual.get(0).getServiceOperation(), is(PUSH));
    }

    @Test
    public void it_reads_default_service_role() {
        assertThat(actual.get(0).getServiceRole(), is(CONSUMER));
    }

    @Test
    public void it_reads_default_service_type() {
        assertThat(actual.get(0).getServiceType(), is(VESSEL_SERVICE));
    }

    @Test
    public void it_reads_service_operation() {
        assertThat(actual.get(1).getServiceOperation(), is(PULL));
    }

    @Test
    public void it_reads_service_role() {
        assertThat(actual.get(1).getServiceRole(), is(PROVIDER));
    }

    @Test
    public void it_reads_service_type() {
        assertThat(actual.get(1).getServiceType(), is(CARGO_SERVICE));
    }

}
