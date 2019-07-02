package eu.cise.signature;

import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.datamodel.v1.entity.vessel.VesselType;
import eu.cise.servicemodel.v1.message.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import static eu.cise.servicemodel.v1.message.InformationSecurityLevelType.NON_CLASSIFIED;
import static eu.cise.servicemodel.v1.message.InformationSensitivityType.AMBER;
import static eu.cise.servicemodel.v1.message.PriorityType.HIGH;
import static eu.cise.servicemodel.v1.message.PurposeType.BORDER_MONITORING;
import static eu.cise.servicemodel.v1.message.ResponseCodeType.SUCCESS;
import static eu.cise.servicemodel.v1.service.ServiceOperationType.PULL;
import static eu.eucise.helpers.PullResponseBuilder.newPullResponse;
import static eu.eucise.helpers.ServiceBuilder.newService;
import static java.nio.charset.StandardCharsets.UTF_8;

class Scenario {

    static String file(String name) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(Scenario.class.getResource(name).toURI())),
                          UTF_8);
    }


    static Message buildMessage() {
        String uuid = UUID.randomUUID().toString();
        return newPullResponse()
                .contextId(uuid)
                .correlationId(uuid)
                .creationDateTime(new Date())
                .id(uuid)
                .priority(HIGH)
                .isRequiresAck(false)
                .sender(newService().id("es.gc-ls01.maritimesafetyincident.pullresponse.gcs04")
                                .operation(PULL))
                .resultCode(SUCCESS)
                .recipient(newService().id("myService2")
                                   .operation(PULL))
                .addEntity(buildVessel())
                .informationSecurityLevel(NON_CLASSIFIED)
                .informationSensitivity(AMBER)
                .isPersonalData(false)
                .purpose(BORDER_MONITORING)
                .retentionPeriod(new Date())
                .build();
    }

    private static Vessel buildVessel() {
        Vessel v = new Vessel();
        v.getNames().add("The Mother Queen");
        v.setDeadweight(30);
        v.setDraught(30.4);
        v.setGrossTonnage(34.5);
        v.setMMSI(45545454L);
        v.setIMONumber(435454L);
        v.setLength(54.56);
        v.setNavigationalStatus(NavigationalStatusType.ENGAGED_IN_FISHING);
        v.getShipTypes().add(VesselType.FISHING_VESSEL);
        v.setYearBuilt(1978);
        return v;
    }
}
