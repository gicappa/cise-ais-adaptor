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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.server.TcpServerExtension;
import eu.cise.adaptor.server.TcpWorkerFactory;
import eu.cise.adaptor.sources.AuthTcpStreamGenerator;
import java.net.Socket;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TcpServerExtension.class)
public class AuthTcpStreamGeneratorTest {

    private final TcpWorkerFactory workerFactory;

    public AuthTcpStreamGeneratorTest(TcpWorkerFactory workerFactory) {
        this.workerFactory = workerFactory;
    }

    @Test
    public void it_waits_for_the_first_ais_message_in_the_stream() {
        try {
            var streamGenerator
                = new AuthTcpStreamGenerator("localhost", 64738, new Socket());

            streamGenerator.generate().forEach(ais -> System.out.println("RCVD[" + ais + "]"));

        } catch (Exception e) {
            e.printStackTrace();
            fail("something went wrong with the protocol");
        }
    }

    @Test
    @Disabled
    public void it_exits_if_the_login_string_is_wrong_or_missing() {
        workerFactory.setAuthString("AUTH=wrong:wrong");

        var streamGenerator
            = new AuthTcpStreamGenerator("localhost", 64738, new Socket());

        assertThrows(AdaptorException.class, streamGenerator::generate);
    }
}
