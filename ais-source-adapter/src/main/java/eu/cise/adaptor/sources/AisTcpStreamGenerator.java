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

package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import org.aeonbits.owner.ConfigFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.stream.Stream;

/**
 * This stream generator connects to a TCP sockets and reads line by line the
 * AIS Message information in a textual format. Each message is separated by the
 * others through a line feed (LF) character and as soon as is read is sent to
 * the {@link java.util.stream.Stream} of {@link String}.
 */
@SuppressWarnings("unused")
public class AisTcpStreamGenerator implements AisStreamGenerator {

    private static final AisTcpAdaptorConfig config
            = ConfigFactory.create(AisTcpAdaptorConfig.class);
    private final Socket socket;
    private InputStreamToStream inputStreamToStream = new InputStreamToStream();

    /**
     * Constructing the class reading host and port from the configuration.
     *
     * @throws AdaptorException when the hostname is not recognised or connection can't be
     *                          established
     */
    public AisTcpStreamGenerator() {
        this(config.getAISSourceSocketHost(), config.getAISSourceSocketPort(), new Socket());
    }

    /**
     * Constructing the class specifying which server host and port to connect to.
     *
     * @param host hostname or ip address where the TCP socket should be opened.
     * @param port port to open to get the AIS messages information.
     * @throws AdaptorException when the hostname is not recognised or connection can't be
     *                          established.
     */
    public AisTcpStreamGenerator(String host, Integer port, Socket socket) {
        try {
            this.socket = socket;
            this.socket.connect(new InetSocketAddress(InetAddress.getByName(host), port));
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }

    /**
     * @return a Stream of Strings each of them containing an AIS message
     */
    public Stream<String> generate() {
        try {
            return inputStreamToStream.stream(socket.getInputStream());
        } catch (IOException e) {
            throw new AdaptorException(e);
        }
    }
}

