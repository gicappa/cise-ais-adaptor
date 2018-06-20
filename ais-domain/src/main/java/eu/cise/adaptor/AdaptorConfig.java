package eu.cise.adaptor;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * This file is containing the adaptor application configuration with different
 * details about the senders and receivers services.
 */
@Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface AdaptorConfig extends Config {

    /**
     * In the CISE demo environment of the JRC is needed that all the vessels
     * have an IMO number (PK in a database). To avoid breaking the demo when
     * this flag is set to true the MMSI number will be copied over to the IMO
     * number.
     *
     * @return true if is a cise demo internal to JRC false otherwise
     */
    @Key("demo-environment")
    boolean isDemoEnvironment();

    /**
     * Setting this property to true will override the timestamp coming from the
     * AIS message with the current processing time. This could be necessary for
     * systems with strict policies or filtering of messaging non belonging
     *
     * @return true if the timestamp should be regenerated false otherwise
     */
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
