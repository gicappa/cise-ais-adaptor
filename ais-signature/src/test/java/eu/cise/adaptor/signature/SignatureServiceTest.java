package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.datamodel.v1.entity.vessel.NavigationalStatusType;
import eu.cise.datamodel.v1.entity.vessel.Vessel;
import eu.cise.datamodel.v1.entity.vessel.VesselType;
import eu.cise.servicemodel.v1.message.*;
import eu.cise.servicemodel.v1.service.Service;
import eu.cise.servicemodel.v1.service.ServiceOperationType;
import eu.eucise.helpers.DateHelper;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.DefaultXmlValidator;
import eu.eucise.xml.XmlMapper;
import eu.eucise.xml.XmlValidator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;


public class SignatureServiceTest {
    public SignatureService sigService;

    @Before
    public void setupTest() {
        sigService = new DefaultSignatureService(
                new DefaultCertificateRegistry(new PrivateKeyInfo("eu.cise.es.gc-ls01", "cisecise"),
                        new KeyStoreInfo("cisePrivate.jks", "cisecise"),
                        new KeyStoreInfo("cisePublic.jks", "cisecise")));
    }

    @Test
    public void when_aaa() {
        SignatureHelper sh = new SignatureHelper();
        XmlMapper xmlMapper = new DefaultXmlMapper();
        XmlValidator xmlValidator = new DefaultXmlValidator();

        Message msg = buildMessage();

        Message signedMsg = sh.sign("eu.cise.es.gc-ls01", msg);

        String messageXML = xmlMapper.toXML(signedMsg);

        xmlValidator.validate(messageXML);

        signedMsg = xmlMapper.fromXML(messageXML);

        sigService.verifySignature(signedMsg);
    }

    @Test
    public void test_signing_and_verification_of_signature() throws Exception {
        long time_1 = System.currentTimeMillis();
        Message msg = buildMessage();
        Message signedMsg = sigService.sign(msg);
        sigService.verifySignature(signedMsg);
        long time_2 = System.currentTimeMillis();
        System.out.println("Operation took " + (time_2 - time_1) + "ms");
    }

    @Test
    public void test_verification_of_signature_on_LC_Message() throws Exception {
        String messageXML = new String(Files.readAllBytes(Paths.get(getClass().getResource("/SignedPushVessels.xml").toURI())), "UTF-8");
        Message signedMsg = new DefaultXmlMapper.PrettyNotValidating().fromXML(messageXML);
        sigService.verifySignature(signedMsg);
    }


    @Test(expected = AISAdaptorException.class)
    public void test_verification_fails_if_message_was_tampered_with() {
        long time_1 = System.currentTimeMillis();
        Message msg = buildMessage();
        Message signedMsg = sigService.sign(msg);
        signedMsg.setMessageID("lulu");
        sigService.verifySignature(signedMsg);
        long time_2 = System.currentTimeMillis();
        System.out.println("Operation took " + (time_2 - time_1) + "ms");
    }

    @Test(expected = AISAdaptorException.class)
    public void test_verification_fails_if_payload_was_tampered_with() {
        long time_1 = System.currentTimeMillis();
        Message msg = buildMessage();
        Message signedMsg = sigService.sign(msg);
        ((Vessel) ((XmlEntityPayload) signedMsg.getPayload()).getAnies().get(0)).setDeadweight(88);
        sigService.verifySignature(signedMsg);
        long time_2 = System.currentTimeMillis();
        System.out.println("Operation took " + (time_2 - time_1) + "ms");
    }


    private Message buildMessage() {
        PullResponse msg = new PullResponse();
        String uuid = UUID.randomUUID().toString();
        msg.setContextID(uuid);
        msg.setCorrelationID(uuid);
        msg.setCreationDateTime(DateHelper.toXMLGregorianCalendar(new Date()));
        msg.setMessageID(uuid);
        msg.setPriority(PriorityType.HIGH);
        msg.setRequiresAck(false);
        Service sender = new Service();
        sender.setServiceID("es.gc-ls01.maritimesafetyincident.pullresponse.gcs04");
        sender.setServiceOperation(ServiceOperationType.PULL);
        msg.setSender(sender);
        msg.setResultCode(ResponseCodeType.SUCCESS);

        Service receiver = new Service();
        receiver.setServiceID("myService2");
        receiver.setServiceOperation(ServiceOperationType.PULL);
        msg.setRecipient(receiver);

        XmlEntityPayload payload = new XmlEntityPayload();
        payload.setInformationSecurityLevel(InformationSecurityLevelType.NON_CLASSIFIED);
        payload.setInformationSensitivity(InformationSensitivityType.AMBER);
        payload.setIsPersonalData(false);
        payload.setPurpose(PurposeType.BORDER_MONITORING);
        payload.setRetentionPeriod(DateHelper.toXMLGregorianCalendar(new Date()));
        payload.setEnsureEncryption(false);


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

        payload.getAnies().add(v);

        msg.setPayload(payload);
        return msg;
    }

}
