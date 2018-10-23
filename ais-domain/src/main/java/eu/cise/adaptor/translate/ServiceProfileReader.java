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

package eu.cise.adaptor.translate;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.servicemodel.v1.authority.CommunityType;
import eu.cise.servicemodel.v1.authority.CountryType;
import eu.cise.servicemodel.v1.authority.FunctionType;
import eu.cise.servicemodel.v1.authority.SeaBasinType;
import eu.cise.servicemodel.v1.service.*;
import org.aeonbits.owner.ConfigFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static eu.cise.servicemodel.v1.service.ServiceOperationType.PUSH;
import static eu.cise.servicemodel.v1.service.ServiceRoleType.CONSUMER;
import static eu.cise.servicemodel.v1.service.ServiceType.VESSEL_SERVICE;
import static java.util.Collections.singletonMap;

public class ServiceProfileReader {

//    profile.0.service_id=
//    profile.0.community=Customs
//    profile.0.country=ES
//    profile.0.data_freshness=NearlyRealTime
//    profile.0.function=CustomsMonitoring
//    profile.0.sea_basin=ArcticOcean
//    profile.0.service_operation=Push
//    profile.0.service_role=Consumer
//    profile.0.service_type=VesselService

    public List<ServiceProfile> list() {
        List<ServiceProfile> profiles = new ArrayList<>();

        AdaptorConfig config;

        for (int i = 0; isProfileNumDefined(config = createConfigNum(i)); i++) {
            profiles.add(readProfileFrom(config));
        }

        return profiles;
    }

    private ServiceProfile readProfileFrom(AdaptorConfig config) {
        ServiceProfile profile = new ServiceProfile();

        profile.setServiceID(config.getProfileServiceId());

        if (config.getProfileCommunity() != null)
            profile.setCommunity(CommunityType.fromValue(config.getProfileCommunity()));

        if (config.getProfileCountry() != null)
            profile.setCountry(CountryType.fromValue(config.getProfileCountry()));

        if (config.getProfileDataFreshness() != null)
            profile.setDataFreshness(DataFreshnessType.fromValue(config.getProfileDataFreshness()));

        if (config.getProfileFunction() != null)
            profile.setFunction(FunctionType.fromValue(config.getProfileFunction()));

        if (config.getProfileSeaBasin() != null)
            profile.setSeaBasin(SeaBasinType.fromValue(config.getProfileSeaBasin()));

        if (config.getProfileServiceOperation() != null)
            profile.setServiceOperation(ServiceOperationType.fromValue(config.getProfileServiceOperation()));
        else
            profile.setServiceOperation(PUSH);

        if (config.getProfileServiceRole() != null)
            profile.setServiceRole(ServiceRoleType.fromValue(config.getProfileServiceRole()));
        else
            profile.setServiceRole(CONSUMER);

        if (config.getProfileServiceType() != null)
            profile.setServiceType(ServiceType.fromValue(config.getProfileServiceType()));
        else
            profile.setServiceType(VESSEL_SERVICE);

        return profile;
    }

    private boolean isProfileNumDefined(AdaptorConfig config) {
        return config.getProfileServiceId() != null ||
                config.getProfileCommunity() != null ||
                config.getProfileCountry() != null ||
                config.getProfileDataFreshness() != null ||
                config.getProfileFunction() != null ||
                config.getProfileSeaBasin() != null;
    }

    private AdaptorConfig createConfigNum(int i) {
        return ConfigFactory.create(AdaptorConfig.class, profileNumberKey(i));
    }

    private Map<String, String> profileNumberKey(int i) {
        return singletonMap("profile.number", String.valueOf(i));
    }
}
