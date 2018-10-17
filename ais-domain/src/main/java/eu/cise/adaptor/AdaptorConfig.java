package eu.cise.adaptor;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.converters.DurationConverter;

import java.time.Duration;

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

    /**
     * Address of the gateway that will receive the messages from the current
     * adaptor.
     *
     * @return the string with the gateway address
     */
    @Key("gateway.address")
    String getGatewayAddress();

    /**
     * Service Id of the recipient that should receive the messages.
     *
     * @return the string with the service id
     */
    @Key("recipient.service.id")
    String getRecipientServiceId();

    /**
     * Service operation of the recipient that should receive the messages.
     * It can be 'PullRequest', 'PullResponse', 'Push'.
     * If unsure use 'Push'
     *
     * @return the string with the service operation
     */
    @Key("recipient.service.operation")
    String getRecipientServiceOperation();

    /**
     * Service Id of the sender public authority that is sending the messages.
     *
     * @return the string with the service id
     */
    @Key("sender.service.id")
    String getServiceId();

    /**
     * A parameter indicating the data freshness to inform the receivers.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the data freshness.
     */
    @Key("sender.service.data-freshness-type")
    String getDataFreshnessType();

    /**
     * A parameter indicating the sea basin of the receivers.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the sea basin.
     */
    @Key("sender.service.sea-basin-type")
    String getSeaBasinType();

    /**
     * A parameter indicating the url of the Legacy System sender service.
     *
     * @return the string with the sender service url.
     */
    @Key("sender.service.participant.url")
    String getEndpointUrl();

    /**
     * Service operation of the sender that sending the messages.
     * It can be 'PullRequest', 'PullResponse', 'Push'.
     * If unsure use 'Push'
     *
     * @return the string with the service operation
     */
    @Key("sender.service.operation")
    String getServiceOperation();

    /**
     * A parameter indicating the priority of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the priority
     */
    @Key("message.priority")
    String getMessagePriority();

    /**
     * A parameter indicating the security level of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the security level
     */
    @Key("message.security-level")
    String getSecurityLevel();

    /**
     * A parameter indicating the sensitivity of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the sensitivity
     */
    @Key("message.sensitivity")
    String getSensitivity();

    /**
     * A parameter indicating the purpose of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the purpose
     */
    @Key("message.purpose")
    String getPurpose();

    /**
     * A parameter indicating the idle time of the message.
     * Please refer to the entity data model to know the possible values.
     *
     * @return the string with the idle time
     */
    @Key("processing.idle.time")
    long getProcessingIdleTime();

    /**
     * A parameter indicating the number of entities that will be sent to the
     * sender in a single CISE message.
     *
     * @return the number of entities
     */
    @Key("processing.entities-per-message")
    int getNumberOfEntitiesPerMessage();

    /**
     * A parameter indicating the number of milliseconds to wait before sending a message when the
     * processing.entities-per-message has not been reached and the buffer of entities is not full.
     * It is needed to avoid to wait indefinitely if the processing.entities-per-message is not
     * reached.
     *
     * @return the timeout to wait before sending a message
     */
    @ConverterClass(DurationConverter.class)
    @Key("processing.sending.timeout")
    Duration getEntityBufferTimeout();

}
