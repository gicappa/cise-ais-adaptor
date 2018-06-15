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

/**
 * This is an interface to define an adapter of a RESTful client in order to
 * communicate with external services.
 *<p>
 * Creating an adapter is important in order to use any kind of HTTP client
 * is desired or necessary to create the actual connection.
 */
@SuppressWarnings("unused")
public interface RestClient {

    /**
     * A method to perform POST requests to a server connecting to an address
     * transmitting  a string payload
     *
     * @param address the address to contact to deliver the request
     * @param payload the payload to be delivered
     * @return the {@link eu.cise.adaptor.RestResult}
     */
    RestResult post(String address, String payload);

    /**
     * A method to perform GET requests to a server address
     *
     * @param address the address to contact to deliver the request
     * @return the {@link eu.cise.adaptor.RestResult}
     */
    RestResult get(String address);

    /**
     * A method to perform DELETE requests to a server address
     *
     * @param address the address to contact to deliver the request
     * @return the {@link eu.cise.adaptor.RestResult}
     */
    RestResult delete(String address);

}