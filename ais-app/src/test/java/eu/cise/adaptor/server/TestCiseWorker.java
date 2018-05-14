package eu.cise.adaptor.server;

import eu.cise.servicemodel.v1.message.PurposeType;
import eu.cise.servicemodel.v1.service.ServiceOperationType;
import eu.eucise.helpers.AckBuilder;
import eu.eucise.xml.DefaultXmlMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import static eu.cise.servicemodel.v1.message.InformationSecurityLevelType.NON_CLASSIFIED;
import static eu.cise.servicemodel.v1.message.InformationSensitivityType.GREEN;
import static eu.cise.servicemodel.v1.message.PriorityType.HIGH;
import static eu.cise.servicemodel.v1.message.PriorityType.LOW;
import static eu.eucise.helpers.AckBuilder.newAck;
import static eu.eucise.helpers.ServiceBuilder.newService;

/**
 * This class is the Worker thread that will open the file containing the AIS
 * data from the classpath and that will stream on the TCP/IP socket connection
 * passed from the server the data coming from the file.
 */
public class TestCiseWorker extends Thread {

    private final Socket socket;

    public TestCiseWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        System.out.println("AISFileStreamer Interrupted");
    }

    @Override
    public void run() {
        System.out.println("* Streaming the contents to the socket");
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(ciseAck());

        } catch (IOException /*| InterruptedException*/ e) {
            e.printStackTrace();
        }
        System.out.println("* Done.");
    }

    private String ciseAck() {
        return new DefaultXmlMapper().toXML(buildAck().build());
    }

    private AckBuilder buildAck(){
        String uuid  = UUID.randomUUID().toString();
        String na = "N/A";
        return newAck()
                .id(uuid)
                .recipient(newService()
                        .id(na)
                        .operation(ServiceOperationType.ACKNOWLEDGEMENT)
                        .participantId(na)
                        .participantUrl(na))
                .sender(newService()
                        .id(na)
                        .operation(ServiceOperationType.ACKNOWLEDGEMENT)
                        .participantId(na)
                        .participantUrl(na))
                .correlationId(na)
                .creationDateTime(new Date())
                .informationSecurityLevel(NON_CLASSIFIED)
                .informationSensitivity(GREEN)
                .purpose(PurposeType.NON_SPECIFIED)
                .priority(LOW)
                .isRequiresAck(false)
                ;

    }
}
