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
