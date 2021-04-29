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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class AuthTcpWorker implements TcpWorker {

    private final BufferedReader reader;
    private final PrintWriter writer;
    private String authString;
    private final String[] aisMessages = new String[]{
            "!AIVDM,1,1,,B,13P88o@02=OqlrHM6FATwCvf08=E,0*73",
            "!AIVDM,1,1,,A,13P88o@uB=Oqm2<M6EkTvkvp0@@b,0*44",
            "!AIVDM,1,1,,A,13P88o@uB=OqmFPM6DSTukwB0<1G,0*7D",
            "!AIVDM,1,1,,A,13P88o@2j=OqmWHM6CRTuCw`0@NU,0*06"};

    private volatile boolean alive = true;

    public AuthTcpWorker(String authString, Socket socket) {
        this.authString = authString;
        this.reader = getReader(socket);
        this.writer = getWriter(socket);
    }

    @Override
    public void run() {
        try {
            while (alive) {
                handleRequest();
                sleep(100);
            }
            reader.close();
            writer.close();
        } catch (InterruptedException | IOException e) {
            throw new AdaptorException("WORKER: exception", e);
        }
    }

    @Override
    public void handleRequest() throws IOException, InterruptedException {
        String input = readSocket();

        if (input != null && !input.isEmpty() && input.equalsIgnoreCase(authString)) {
            sleep(1000);
            writeSocket("AUTH-OK");

            for (String aisMessage : aisMessages) {
                sleep(1000);
                writeSocket(aisMessage);
            }
        } else {
            sleep(1000);
            writeSocket("AUTH-KO");
        }

        alive = false;
    }

    private void writeSocket(String output) {
        System.out.print(output);
        writer.print(output);
        writer.flush();
    }

    private String readSocket() throws IOException {
        String input = reader.readLine();
        System.out.println("< " + input);
        return input;
    }

    private PrintWriter getWriter(Socket socket) {
        try {
            return new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    private BufferedReader getReader(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }
}
