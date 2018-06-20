package eu.cise.adaptor.translate;

import eu.cise.adaptor.AdaptorConfig;
import eu.cise.datamodel.v1.entity.Entity;
import eu.cise.servicemodel.v1.authority.SeaBasinType;
import eu.cise.servicemodel.v1.message.*;
import eu.cise.servicemodel.v1.service.DataFreshnessType;
import eu.cise.servicemodel.v1.service.ServiceOperationType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static eu.eucise.helpers.ParticipantBuilder.newParticipant;
import static eu.eucise.helpers.PushBuilder.newPush;
import static eu.eucise.helpers.ServiceBuilder.newService;

/**
 * This transformer will map a vessel entity into a cise message ready to be
 * sent out in a HTTP request
 */
public class CiseModelToCiseMessage implements Translator<List<Entity>, Push> {

    private final AdaptorConfig config;

    /**
     * The config is the collaborator of this class.
     *
     * @param config the AdaptorConfig object
     */
    public CiseModelToCiseMessage(AdaptorConfig config) {
        this.config = config;
    }

    /**
     * The method will translate the list of vessel entities into a cise message
     * adding the service model around the payload. Being in a List form the
     * payload can be
     *
     * @param entities a list of vessel entities objects
     * @return a new cise message with the list of entities as a payload
     */
    @Override
    public Push translate(List<Entity> entities) {
        return newPush()
                .id(UUID.randomUUID().toString())
                .contextId(UUID.randomUUID().toString())
                .correlationId(UUID.randomUUID().toString())
                .creationDateTime(new Date())
                .sender(newService()
                        .id(config.getServiceId())
                        .dataFreshness(DataFreshnessType.fromValue(config.getDataFreshnessType()))
                        .seaBasin(SeaBasinType.fromValue(config.getSeaBasinType()))
                        .operation(ServiceOperationType.fromValue(config.getServiceOperation()))
                        .participant(newParticipant().endpointUrl(config.getEndpointUrl())))
                .recipient(newService()
                        .id(config.getRecipientServiceId())
                        .operation(ServiceOperationType.fromValue(config.getRecipientServiceOperation()))
                )
                .priority(PriorityType.fromValue(config.getMessagePriority()))
                .isRequiresAck(false)
                .informationSecurityLevel(InformationSecurityLevelType.fromValue(config.getSecurityLevel()))
                .informationSensitivity(InformationSensitivityType.fromValue(config.getSensitivity()))
                .isPersonalData(false)
                .purpose(PurposeType.fromValue(config.getPurpose()))
                .addEntities(entities)
                .build();
    }

}
