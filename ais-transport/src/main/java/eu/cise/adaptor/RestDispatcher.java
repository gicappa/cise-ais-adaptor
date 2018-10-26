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

package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.DispatchResult;
import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

/**
 * The RestDispatcher performs RESTful request to nodes or legacy systems.
 * The current implementation is just sending CISE Messages
 */
@SuppressWarnings({"WeakerAccess", "Unused"})
public class RestDispatcher implements Dispatcher {

    private final RestClient client;
    private final XmlMapper xmlMapper;

    /**
     * This constructor is called by the class for name
     */
    @SuppressWarnings("unused")
    public RestDispatcher() {
        this(new JerseyRestClient(), new DefaultXmlMapper.NotValidating());
    }

    public RestDispatcher(RestClient client, XmlMapper xmlMapper) {
        this.client = client;
        this.xmlMapper = xmlMapper;
    }

    /**
     * The main responsibility of the class is to send messages by using a
     * RESTful client.
     *
     * @param message message to be sent.
     * @param address gateway address to send the message to
     * @return a {@link eu.cise.adaptor.dispatch.DispatchResult} containing the
     * dispatching results
     */
    @Override
    public DispatchResult send(Message message, String address) {
        String payload = xmlMapper.toXML(message);

        RestResult result = client.post(address, payload);

        return new DispatchResult(result.isOK(), result.getBody());
    }

}
