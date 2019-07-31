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

package eu.cise.adaptor.dispatch;

import eu.cise.adaptor.Dispatcher;
import eu.cise.servicemodel.v1.message.Message;

/**
 * The is a decorator to the generic dispatcher that wraps the runtime
 * exceptions not to have exit the processing flow in case of errors.
 */
public class ErrorCatchingDispatcher implements Dispatcher {

    private final Dispatcher proxy;

    /**
     * Decorator constructor accepting the base class as a collaborator
     *
     * @param proxy is the proxy object that will be invoked while decorating
     *              its behavior
     */
    public ErrorCatchingDispatcher(Dispatcher proxy) {
        this.proxy = proxy;
    }

    /**
     * The send message wraps the proxied send message in a try catch clause
     * to avoid throwing runtime exceptions.
     *
     * @param message message to be sent
     * @param address gateway address to send the message to
     * @return a dispatcher result
     */
    @Override
    public DispatchResult send(Message message, String address) {
        try {
            return proxy.send(message, address);
        } catch (Throwable t) {
            return new DispatchResult(false, t.getMessage());
        }
    }
}
