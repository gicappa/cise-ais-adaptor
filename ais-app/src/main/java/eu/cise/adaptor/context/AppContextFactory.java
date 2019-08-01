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

package eu.cise.adaptor.context;

import eu.cise.adaptor.AppContext;
import eu.cise.adaptor.AdaptorExtConfig;
import eu.cise.adaptor.exceptions.AdaptorException;

/**
 * app-context.type=tcp
 * app-context.type=tcp-auth
 * app-context.type=file
 */
public class AppContextFactory {

    private final AdaptorExtConfig config;

    public AppContextFactory(AdaptorExtConfig config) {
        this.config = config;
    }

    public AppContext create() {
        if (config.getAppContextType() == null)
            throw new AdaptorException(errorMessage("null"));

        switch (config.getAppContextType()) {
            case "file":
                return new FileAppContext(config);
            case "tcp":
                return new TcpAppContext(config);
            case "auth-tcp":
                return new AuthTcpAppContext(config);
            default:
                throw new AdaptorException(errorMessage(config.getAppContextType()));
        }
    }

    private String errorMessage(String msg) {
        return "Invalid app-context.type property in ais-adaptor.properties file [" + msg + "]";
    }

}
