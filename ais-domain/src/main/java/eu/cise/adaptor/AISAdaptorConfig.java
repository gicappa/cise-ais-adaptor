package eu.cise.adaptor;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface AISAdaptorConfig extends Config {

    @Key("demo-environment")
    boolean isDemoEnvironment();

    @Key("override-timestamps")
    boolean isOverridingTimestamps();

    @Key("gateway.address")
    String getGatewayAddress();

    @Key("service.id")
    String getServiceId();

    @Key("service.data-freshness-type")
    String getDataFreshnessType();

    @Key("service.sea-basin-type")
    String getSeaBasinType();

    @Key("service.participant.url")
    String getEndpointUrl();

    @Key("service.operation")
    String getServiceOperation();

    @Key("message.priority")
    String getMessagePriority();

    @Key("message.security-level")
    String getSecurityLevel();

    @Key("message.sensitivity")
    String getSensitivity();

    @Key("message.purpose")
    String getPurpose();

    @Key("processing.idle.time")
    long getProcessingIdleTime();
}
