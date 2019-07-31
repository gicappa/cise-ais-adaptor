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

import eu.cise.signature.certificates.DefaultCertificateRegistry;
import eu.cise.signature.certificates.KeyStoreInfo;
import eu.cise.signature.certificates.PrivateKeyInfo;
import eu.cise.signature.exceptions.SignatureEx;
import eu.cise.signature.signers.DefaultDomSigner;
import eu.cise.signature.verifiers.DefaultDomVerifier;
import eu.eucise.xml.DefaultXmlMapper;
import eu.eucise.xml.XmlMapper;

@SuppressWarnings("unused")
public class SignatureServiceBuilder {

    private static final String KS_NAME_MISSING =
            "To build a SignatureService object it's mandatory to specify a keystore name" +
                    "using the .withKeyStoreName(...) method";
    private static final String KS_PASS_MISSING =
            "To build a SignatureService object it's mandatory to specify a keystore password" +
                    "using the .withKeyStorePassword(...) method";
    private static final String PK_ALIAS_MISSING =
            "To build a SignatureService object it's mandatory to specify a private key alias" +
                    "using the .withPrivateKeyAlias(...) method";
    private static final String PK_PASS_MISSING =
            "To build a SignatureService object it's mandatory to specify a private key password" +
                    "using the .withPrivateKeyPassword(...) method";

    private final XmlMapper xmlMapper;
    private CertificateRegistry registry;
    private String privateKeyAlias;
    private String keyStoreName;
    private String keyStorePassword;
    private String privateKeyPassword;

    // building the class
    private SignatureServiceBuilder(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    public static SignatureServiceBuilder newSignatureService() {
        return newSignatureService(new DefaultXmlMapper());
    }

    public static SignatureServiceBuilder newSignatureService(XmlMapper xmlMapper) {
        return new SignatureServiceBuilder(xmlMapper);
    }

    // adding information to the builder
    public SignatureServiceBuilder withPrivateKeyAlias(String alias) {
        this.privateKeyAlias = alias;
        return this;
    }

    public SignatureServiceBuilder withPrivateKeyPassword(String password) {
        this.privateKeyPassword = password;
        return this;
    }

    public SignatureServiceBuilder withKeyStoreName(String keyStoreName) {
        this.keyStoreName = keyStoreName;
        return this;
    }

    public SignatureServiceBuilder withKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
        return this;
    }

    public SignatureService build() {
        notNullOrEmpty(keyStoreName, KS_NAME_MISSING);
        notNullOrEmpty(keyStorePassword, KS_PASS_MISSING);
        notNullOrEmpty(privateKeyAlias, PK_ALIAS_MISSING);
        notNullOrEmpty(privateKeyPassword, PK_PASS_MISSING);

        KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keyStoreName, keyStorePassword);
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(privateKeyAlias, privateKeyPassword);

        registry = new DefaultCertificateRegistry(keyStoreInfo);

        DomVerifier verifier = new DefaultDomVerifier(registry);
        DomSigner signer = new DefaultDomSigner(registry, privateKeyInfo);

        return new DefaultSignatureService(signer, verifier, xmlMapper);
    }

    private void notNullOrEmpty(String str, String message) {
        if (str == null || str.isEmpty()) throw new SignatureEx(message);
    }
}
