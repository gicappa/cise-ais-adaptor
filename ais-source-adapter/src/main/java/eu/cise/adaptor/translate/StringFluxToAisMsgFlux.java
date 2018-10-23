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

package eu.cise.adaptor.translate;

import eu.cise.adaptor.AisMsg;
import eu.cise.adaptor.StringToAisMsg;
import reactor.core.publisher.Flux;

import java.util.Optional;

@SuppressWarnings("ununsed")
public class StringFluxToAisMsgFlux implements StringToAisMsg {

    private final StringToNmea stringToNmea;
    private final NmeaToAISMessage nmeaToAISMessage;
    private final AisMessageToAisMsg aisMessageToAisMsg;

    public StringFluxToAisMsgFlux() {
        this.stringToNmea = new StringToNmea();
        this.nmeaToAISMessage = new NmeaToAISMessage("SRC");
        this.aisMessageToAisMsg = new AisMessageToAisMsg();
    }

    @SuppressWarnings("ununsed")
    public StringFluxToAisMsgFlux(StringToNmea stringToNmea,
                                  NmeaToAISMessage nmeaToAISMessage,
                                  AisMessageToAisMsg aisMessageToAISMsg) {
        this.stringToNmea = stringToNmea;
        this.nmeaToAISMessage = nmeaToAISMessage;
        this.aisMessageToAisMsg = aisMessageToAISMsg;
    }

    @Override
    public Flux<AisMsg> translate(Flux<String> stringFlux) {
        return stringFlux
                .map(stringToNmea::translate)
                .map(nmeaToAISMessage::translate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(aisMessageToAisMsg::translate);
    }
}
