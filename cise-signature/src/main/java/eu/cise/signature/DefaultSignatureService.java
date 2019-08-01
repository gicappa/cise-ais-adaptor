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

package eu.cise.signature;

import eu.cise.servicemodel.v1.message.Message;
import eu.cise.signature.certificates.DefaultCertificateRegistry;
import eu.cise.signature.signers.DefaultDomSigner;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

/**
 * The default implementation for the interface SignatureService that implement the features of
 * signing and verify the signature of a message
 *
 */
public class DefaultSignatureService implements SignatureService {

    private final DomSigner signer;
    private final DomVerifier verifier;
    private final XmlMapper xmlMapper;

    public DefaultSignatureService(DomSigner signer,
                                   DomVerifier verifier,
                                   XmlMapper xmlMapper) {
        this.signer = signer;
        this.verifier = verifier;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public Message sign(Message message) {
        return xmlMapper.fromDOM(signer.sign(xmlMapper.toDOM(message)));
    }

    @Override
    public void verify(Message message) {
        verifier.verify(xmlMapper.toDOM(message));
    }
}
