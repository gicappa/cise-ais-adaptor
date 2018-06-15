/*
 * Copyright 2018 JRC
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package eu.cise.adaptor;

import eu.cise.adaptor.exceptions.AdaptorException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.function.Function;

/**
 * The implementation of a the RESTful client using the Jersey interface.
 * The implementation uses the {@link javax.ws.rs.client.Client} class and is
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
     * @return a {@link eu.cise.adaptor.RestResult} withe the response details
     */
    @Override
    public RestResult post(String address, String payload) {
        return vestException(address, (a) -> translateResult(targetXml(a).post(Entity.xml(payload))));
    }

    /**
     * Concrete implementation of a GET request using Jersey.
     *
     * @param address the address to contact to deliver the request
     * @return a {@link eu.cise.adaptor.RestResult} withe the response details
     */
    @Override
    public RestResult get(String address) {
        return vestException(address, (a) -> translateResult(targetXml(a).get()));
    }

    /**
     * Concrete implementation of a DELETE request using Jersey.
     *
     * @param address the address to contact to deliver the request
     * @return a {@link eu.cise.adaptor.RestResult} withe the response details
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
