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

package eu.cise.signature.certificates;

import eu.cise.signature.CertificateRegistry;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static eu.cise.signature.exceptions.ExceptionHandler.safe;

@SuppressWarnings("unused")
public class DefaultCertificateRegistry implements CertificateRegistry {

    private final KeyStoreInfo keyStore;
    private Map<String, X509Certificate> publicCertMap = new ConcurrentHashMap<>();

    public DefaultCertificateRegistry(KeyStoreInfo keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public PrivateKey findPrivateKey(PrivateKeyInfo pkInfo) {
        return safe(() -> keyStore.findPrivateKey(pkInfo.keyAlias(), pkInfo.password()));
    }

    @Override
    public X509Certificate findCertificate(String certificateAlias) {
        return safe(() -> {
            if (!publicCertMap.containsKey(certificateAlias)) {
                publicCertMap.put(certificateAlias,
                                  keyStore.findPublicCertificate(certificateAlias));
            }

            return publicCertMap.get(certificateAlias);

        });
    }

}
