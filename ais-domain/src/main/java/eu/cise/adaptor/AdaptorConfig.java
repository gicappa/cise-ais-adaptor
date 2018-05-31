package eu.cise.adaptor;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface AdaptorConfig extends Config {

    @Key("demo-environment")
    boolean isDemoEnvironment();

    @Key("override-timestamps")
    boolean isOverridingTimestamps();

    @Key("gateway.address")
    String getGatewayAddress();

    @Key("recipient.service.id")
    String getRecipientServiceId();

    @Key("recipient.service.operation")
    String getRecipientServiceOperation();

    @Key("sender.service.id")
    String getServiceId();

    @Key("sender.service.data-freshness-type")
    String getDataFreshnessType();

    @Key("sender.service.sea-basin-type")
    String getSeaBasinType();

    @Key("sender.service.participant.url")
    String getEndpointUrl();

    @Key("sender.service.operation")
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

    @Key("processing.entities-per-message")
    int getNumberOfEntitiesPerMessage();
}
