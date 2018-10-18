package eu.cise.adaptor.translate;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.servicemodel.v1.authority.CommunityType;
import eu.cise.servicemodel.v1.authority.CountryType;
import eu.cise.servicemodel.v1.authority.FunctionType;
import eu.cise.servicemodel.v1.authority.SeaBasinType;
import eu.cise.servicemodel.v1.service.DataFreshnessType;
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

    public ServiceProfiles() {
    }

    public List<ServiceProfile> list() {
        List<ServiceProfile> profiles = new ArrayList<>();

        for (int i = 0; i < 1000000; i++) {
            AdaptorConfig config = createConfigNumber(i);

            if (!isProfileNumberDefined(config)) {
                break;
            }

            profiles.add(writeProfileFrom(config));
        }

        return profiles;
    }

    private ServiceProfile writeProfileFrom(AdaptorConfig config) {
        ServiceProfile profile = new ServiceProfile();
        profile.setServiceID(config.getProfileServiceId());
        profile.setCommunity(CommunityType.fromValue(config.getProfileCommunity()));
        profile.setCountry(CountryType.fromValue(config.getProfileCountry()));
        profile.setDataFreshness(DataFreshnessType.fromValue(config.getProfileDataFreshness()));
        profile.setFunction(FunctionType.fromValue(config.getProfileFunction()));
        profile.setSeaBasin(SeaBasinType.fromValue(config.getProfileSeaBasin()));
        return profile;
    }

    private boolean isProfileNumberDefined(AdaptorConfig config) {
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
