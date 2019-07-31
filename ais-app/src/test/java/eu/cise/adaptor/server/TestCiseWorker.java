/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
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

package eu.cise.adaptor.server;

import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.servicemodel.v1.message.AcknowledgementType;
import eu.cise.servicemodel.v1.message.PurposeType;
import eu.cise.servicemodel.v1.service.ServiceOperationType;
import eu.eucise.helpers.AckBuilder;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

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
public class TestCiseWorker implements Runnable {

    private final Socket socket;
    private final Consumer<String> requestConsumer;
    private final XmlMapper xmlMapper;

    public TestCiseWorker(Socket socket, Consumer<String> requestConsumer) {
        this.socket = socket;
        this.requestConsumer = requestConsumer;
        this.xmlMapper = new DefaultXmlMapper();

    }

    @Override
    public void run() {
        try {
            manageInput(socket.getInputStream());
            manageOutput(socket.getOutputStream());
            socket.shutdownInput();
            socket.shutdownOutput();
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    private void manageOutput(OutputStream outputStream) {
        PrintWriter out = new PrintWriter(outputStream, false);
        out.println(buildResponse(ciseAck()));
        out.flush();
    }

    private void manageInput(InputStream inputStream) {
        requestConsumer.accept(convertStreamToString(inputStream));
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

    private String convertStreamToString(InputStream is) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int contentLength = -1;
            while (!(line = reader.readLine()).isEmpty()) {
                if (line.indexOf("Content-Length: ") > -1) {
                    contentLength = Integer.valueOf(line.substring("Content-Length: ".length()));
                }
            }

            if (contentLength == -1) {
                throw new AdaptorException("No HTTP Header 'Content-Length' specified while it's mandatory");
            }

            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            return new String(body);
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
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
