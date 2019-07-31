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

package eu.cise.adaptor.translate;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.ais.messages.Metadata;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import eu.cise.adaptor.exceptions.AdaptorException;

import java.util.ArrayList;
import java.util.Optional;

public class NmeaToAISMessage implements Translator<NMEAMessage, Optional<AISMessage>> {


    private final String source;
    private final ArrayList<NMEAMessage> messageFragments = new ArrayList<>();

    public NmeaToAISMessage(String source) {
        this.source = source;
    }

    @Override
    public Optional<AISMessage> translate(NMEAMessage nmeaMessage) {
        try {
            if (!nmeaMessage.isValid()) {
                throw new AdaptorException("NMEA to AISMessage transformation error");
            }

            int numberOfFragments = nmeaMessage.getNumberOfFragments();
            if (numberOfFragments <= 0) {
                messageFragments.clear();
                return Optional.empty();
            }

            if (numberOfFragments == 1) {
                messageFragments.clear();
                return Optional.of(AISMessage.create(new Metadata(source), nmeaMessage));
            }

            int fragmentNumber = nmeaMessage.getFragmentNumber();
            if (fragmentNumber < 0) {
                messageFragments.clear();
                return Optional.empty();
            }

            if (fragmentNumber > numberOfFragments) {
                messageFragments.clear();
                return Optional.empty();
            }

            int expectedFragmentNumber = messageFragments.size() + 1;
            if (expectedFragmentNumber != fragmentNumber) {
                messageFragments.clear();
                return Optional.empty();
            }

            messageFragments.add(nmeaMessage);

            if (nmeaMessage.getNumberOfFragments() == messageFragments.size()) {
                AISMessage aisMessage
                        = AISMessage.create(new Metadata(source),
                                            messageFragments.toArray(new NMEAMessage[messageFragments.size()]));

                messageFragments.clear();
                return Optional.of(aisMessage);
            }

            return Optional.empty();

        } catch (Exception e) {
            // It catches possible exceptions like InvalidMessage or similar
            return Optional.empty();
        }
    }
}
