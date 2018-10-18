package eu.cise.adaptor.translate;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.servicemodel.v1.authority.CommunityType;
import eu.cise.servicemodel.v1.service.ServiceProfile;
import org.aeonbits.owner.ConfigFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class ServiceProfiles {

//    profile.0.service_id=
//    profile.0.community=Customs
//    profile.0.country=ES
//    profile.0.data_freshness=NearlyRealTime
//    profile.0.function=CustomsMonitoring
//    profile.0.sea_basin=ArcticOcean
//    profile.0.service_operation=Push
//    profile.0.service_role=Consumer
//    profile.0.service_type=VesselService

//    profile.0.service_operation=Push
//    profile.0.service_role=Consumer
//    profile.0.service_type=VesselService

    private AdaptorConfig config;

    public ServiceProfiles(AdaptorConfig config) {
        this.config = config;
    }

    public List<ServiceProfile> list() {
        List<ServiceProfile> profiles = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            config = createConfigNumber(i);

            if (isProfileNumberDefined()) {
                ServiceProfile profile = new ServiceProfile();
                profile.setServiceID(config.getProfileServiceId());
                profile.setCommunity(CommunityType.fromValue(config.getProfileCommunity()));

                profiles.add(profile);
            }
        }

        return profiles;
    }

    private boolean isProfileNumberDefined() {
        return config.getProfileServiceId() != null ||
                config.getProfileCommunity() != null ||
                config.getProfileCountry() != null ||
                config.getProfileDataFreshness() != null ||
                config.getProfileFunction() != null ||
                config.getProfileSeaBasin() != null;
    }

    private AdaptorConfig createConfigNumber(int i) {
        return ConfigFactory.create(AdaptorConfig.class, profileNumberKey(i));
    }

    private Map<String, String> profileNumberKey(int i) {
        return singletonMap("profile.number", String.valueOf(i));
    }
}
