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
import eu.cise.datamodel.v1.entity.Entity;
import eu.cise.servicemodel.v1.authority.SeaBasinType;
import eu.cise.servicemodel.v1.message.*;
import eu.cise.servicemodel.v1.service.DataFreshnessType;
import eu.cise.servicemodel.v1.service.ServiceOperationType;
import eu.eucise.helpers.PushBuilder;
import eu.eucise.helpers.ServiceBuilder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static eu.cise.servicemodel.v1.service.ServiceOperationType.SUBSCRIBE;
import static eu.cise.servicemodel.v1.service.ServiceType.VESSEL_SERVICE;
import static eu.eucise.helpers.ParticipantBuilder.newParticipant;
import static eu.eucise.helpers.PushBuilder.newPush;
import static eu.eucise.helpers.ServiceBuilder.newService;

/**
 * This transformer will map a vessel entity into a cise message ready to be
 * sent out in a HTTP request
 */
public class VesselToPushMessage implements Translator<List<Entity>, Push> {

    private final AdaptorConfig config;
    private final ServiceProfileReader profiles;

    /**
     * The config is the collaborator of this class.
     *
     * @param config the AdaptorConfig object
     */
    public VesselToPushMessage(AdaptorConfig config, ServiceProfileReader profiles) {
        this.config = config;
        this.profiles = profiles;
    }

    /**
     * The method will translate the list of vessel entities into a cise message
     * adding the service model around the payload.
     * <p>
     * If the config.getServiceOperation() is equals to Subscribe the message will
     * be sent without recipient nor DiscoveryProfile according to the document
     * "EUCISE2020 Interface Control Document x National Adaptors.pdf".
     * Otherwise the adaptor will assume that the message must be sent as a normal
     * Push and the profile list will be parsed and added to the message.
     *
     * @param entities a list of vessel entities objects
     * @return a new cise message with the list of entities as a payload
     */
    @Override
    public Push translate(List<Entity> entities) {
        PushBuilder message = translateCommon(entities);

        if (isSubscribeMessage()) {
            return message.recipient(createSubscriptionRecipient()).build();
        } else {
            return message.addProfiles(profiles.list()).build();
        }

    }

    /**
     * Create a recipient for the case of a Subscription Push message
     * that is required (even if it's useless) by the parsing and
     * verification of the node.
     *
     * @return a ServiceBuilder with the subscriber id the service type and operation
     */
    private ServiceBuilder createSubscriptionRecipient() {
        return newService()
                .id(config.getSubscribeServiceId())
                .operation(SUBSCRIBE)
                .type(VESSEL_SERVICE);
    }

    /**
     * Actually create the message with all the details specified by the configuration
     * file.
     *
     * @param entities a list of vessel entities objects
     * @return a new cise message with the list of entities as a payload
     */
    private PushBuilder translateCommon(List<Entity> entities) {
        return newPush()
                .id(UUID.randomUUID().toString())
                .contextId(UUID.randomUUID().toString())
                .correlationId(UUID.randomUUID().toString())
                .creationDateTime(new Date())
                .sender(newService()
                                .id(config.getServiceId())
                                .type(VESSEL_SERVICE)
                                .dataFreshness(DataFreshnessType.fromValue(config.getDataFreshnessType()))
                                .seaBasin(SeaBasinType.fromValue(config.getSeaBasinType()))
                                .operation(ServiceOperationType.fromValue(config.getServiceOperation()))
                                .participant(newParticipant().endpointUrl(config.getEndpointUrl())))
                .priority(PriorityType.fromValue(config.getMessagePriority()))
                .isRequiresAck(false)
                .informationSecurityLevel(InformationSecurityLevelType.fromValue(config.getSecurityLevel()))
                .informationSensitivity(InformationSensitivityType.fromValue(config.getSensitivity()))
                .isPersonalData(false)
                .purpose(PurposeType.fromValue(config.getPurpose()))
                .addEntities(entities);
    }

    /**
     * True if  the service operation of the sender is 'Subscribe'
     *
     * @return true for subscription protocol
     */
    private boolean isSubscribeMessage() {
        return ServiceOperationType.fromValue(config.getServiceOperation()).equals(SUBSCRIBE);
    }
}
