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

package eu.cise.adaptor;

import eu.cise.adaptor.exceptions.AdaptorException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.function.Function;

/**
 * The implementation of a RESTful client using the Jersey interface.
 * The implementation uses the {@link jakarta.ws.rs.client.Client} class and is
 * bound to send an XML payload (application/xml media type).
 */
public class JerseyRestClient implements RestClient {

    private final Client client;

    public JerseyRestClient() {
        this(ClientBuilder.newClient());
    }

    public JerseyRestClient(Client client) {
        this.client = client;
    }

    /**
     * Concrete implementation of a post request using Jersey.
     *
     * @param address the address to contact to deliver the request
     * @param payload the payload to be delivered
     * @return a {@link eu.cise.adaptor.RestResult} with the response details
     */
    @Override
    public RestResult post(String address, String payload) {
        return vestException(address, (a) -> translateResult(targetXml(a).post(Entity.xml(payload))));
    }

    /**
     * Concrete implementation of a GET request using Jersey.
     *
     * @param address the address to contact to deliver the request
     * @return a {@link eu.cise.adaptor.RestResult} with the response details
     */
    @Override
    public RestResult get(String address) {
        return vestException(address, (a) -> translateResult(targetXml(a).get()));
    }

    /**
     * Concrete implementation of a DELETE request using Jersey.
     *
     * @param address the address to contact to deliver the request
     * @return a {@link eu.cise.adaptor.RestResult} with the response details
     */
    @Override
    public RestResult delete(String address) {
        return vestException(address, (a) -> translateResult(targetXml(a).delete()));
    }

    // PRIVATE //
    private Invocation.Builder targetXml(String address) {
        return client.target(address).request(MediaType.APPLICATION_XML);
    }

    private RestResult translateResult(Response r) {
        return new RestResult(r.getStatus(), r.readEntity(String.class),
                r.getStatusInfo().getReasonPhrase());
    }

    private RestResult vestException(String address, Function<String, RestResult> function) {
        try {
            return function.apply(address);
        } catch (Throwable t) {
            throw new AdaptorException("Error while connecting to address|" + address, t);
        }
    }

}
