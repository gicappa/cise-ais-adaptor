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

import eu.cise.adaptor.*;
import eu.cise.adaptor.AdaptorLogger.Slf4j;
import eu.cise.adaptor.dispatch.ErrorCatchingDispatcher;
import eu.cise.adaptor.signature.SignatureDispatcherDecorator;
import eu.cise.adaptor.translate.AisMsgToVessel;
import eu.cise.adaptor.translate.ServiceProfileReader;
import eu.cise.adaptor.translate.StringFluxToAisMsgFlux;
import eu.cise.adaptor.translate.VesselToPushMessage;
import eu.cise.signature.SignatureService;

import static eu.cise.signature.SignatureServiceBuilder.newSignatureService;

/**
 *
 */
public abstract class AbstractAppContext implements AppContext {

    private final AdaptorExtConfig config;
    private final Slf4j logger;

    AbstractAppContext(AdaptorExtConfig config) {
        this.config = config;
        this.logger = new Slf4j();
    }

    @Override
    public abstract AisStreamGenerator makeSource();

    @Override
    public DefaultPipeline makeStreamProcessor() {
        return new DefaultPipeline(
                new StringFluxToAisMsgFlux(logger),
                new AisMsgToVessel(config),
                new VesselToPushMessage(config, new ServiceProfileReader()),
                config);
    }

    @Override
    public AdaptorLogger makeLogger() {
        return logger;
    }

    @Override
    public Dispatcher makeDispatcher() {
        return new SignatureDispatcherDecorator(makeRestDispatcher(), makeSignatureService());
    }

    private SignatureService makeSignatureService() {
        return newSignatureService()
                .withKeyStoreName(config.getKeyStoreName())
                .withKeyStorePassword(config.getKeyStorePassword())
                .withPrivateKeyAlias(config.gePrivateKeyAlias())
                .withPrivateKeyPassword(config.getPrivateKeyPassword())
                .build();
    }

    private Dispatcher makeRestDispatcher() {
        return new ErrorCatchingDispatcher(new RestDispatcher());
    }

}
