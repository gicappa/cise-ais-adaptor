package eu.cise.adaptor.server;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.servicemodel.v1.message.AcknowledgementType;
import eu.cise.servicemodel.v1.message.PurposeType;
import eu.cise.servicemodel.v1.service.ServiceOperationType;
import eu.eucise.helpers.AckBuilder;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;

import static eu.cise.servicemodel.v1.message.InformationSecurityLevelType.NON_CLASSIFIED;
import static eu.cise.servicemodel.v1.message.InformationSensitivityType.GREEN;
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
    private final XmlMapper xmlMapper;

    public TestCiseWorker(Socket socket) {
        this.socket = socket;
        this.xmlMapper = new DefaultXmlMapper();
    }

    @Override
    public void run() {
        try {
            manageInput(socket.getInputStream());
            socket.shutdownInput();
            manageOutput(socket.getOutputStream());
            socket.shutdownOutput();

        } catch (IOException /*| InterruptedException*/ e) {
            throw new AISAdaptorException(e);
        }
    }

    private void manageOutput(OutputStream outputStream) {
        PrintWriter out = new PrintWriter(outputStream, false);
        out.println(buildResponse(ciseAck()));
        out.flush();
    }

    private void manageInput(InputStream inputStream) throws IOException {
        xmlMapper.fromXML(convertStreamToString(inputStream));
    }

    private String buildResponse(String body) {
        StringBuffer resBuffer = new StringBuffer();
        resBuffer.append("HTTP/1.1 200 OK\n");
        resBuffer.append("Content-Type: application/xml\n");
        resBuffer.append("Content-Length: ").append(body.length()).append("\n");
        resBuffer.append("Expires: Wed, 16 May 2018 09:59:24 GMT\n");
        resBuffer.append("Date: Wed, 16 May 2018 09:59:03 GMT\n");
        resBuffer.append("Server: TestServer\n\n");
        resBuffer.append(body);
        return resBuffer.toString();
    }

    private String ciseAck() {
        return xmlMapper.toXML(buildAck().build());
    }

    private String convertStreamToString(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();

        while (is.available() > 0) {
            sb.append((char) is.read());
        }

        return sb.toString();
    }

    private synchronized String extractBody(String request) {
        String[] lines = request.split(System.getProperty("line.separator"));
        StringBuffer sbuf = new StringBuffer();
        boolean isHeader = true;
        for (int i = 0; i < lines.length; i++) {
            if (isHeader && lines[i].isEmpty()) {
                isHeader = false;
                continue;
            }
            sbuf.append(lines[i]);
        }
        return sbuf.toString();
    }

    private AckBuilder buildAck() {
        String uuid = UUID.randomUUID().toString();
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
                .ackCode(AcknowledgementType.SUCCESS)
                ;

    }
}
