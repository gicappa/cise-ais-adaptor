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

package eu.cise.adaptor.signature;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static java.nio.charset.StandardCharsets.UTF_8;

public class XXXTest {

    String message
            = "MIIFPzCCBCegAwIBAgICEAEwDQYJKoZIhvcNAQELBQAwga0xCzAJBgNVBAYTAmVzMRIwEAYKCZIm" +
            "\n" +
            "iZPyLGQBGRYCZXUxFDASBgoJkiaJk/IsZAEZFgRjaXNlMR4wHAYDVQQKDBVldS5jaXNlLmVzIHNp\n" +
            "Z25pbmcgQ0ExFDASBgNVBAsMC0pSQyBUZXN0QmVkMR4wHAYDVQQLDBVldS5jaXNlLmVzIHNpZ25p\n" +
            "bmcgQ0ExHjAcBgNVBAMMFWV1LmNpc2UuZXMgc2lnbmluZyBDQTAeFw0xODAyMjExMDE2NTdaFw0x\n" +
            "OTAzMDMxMDE2NTdaMIGVMQswCQYDVQQGEwJlczESMBAGCgmSJomT8ixkARkWAmV1MRQwEgYKCZIm\n" +
            "iZPyLGQBGRYEY2lzZTEQMA4GA1UECgwHZ2MtbHMwMTEXMBUGA1UECwwObGVnYWN5IHN5c3RlbXMx\n" +
            "FDASBgNVBAsMC0pSQyBUZXN0QmVkMRswGQYDVQQDDBJldS5jaXNlLmVzLmdjLWxzMDEwggEiMA0G\n" +
            "CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCtAOY+JoY3ltfBrCnAPwiUhedJnveqnxNCEh6NHUGE\n" +
            "qkk1a9OqhE6pzQTcWCVkeQrH49epFxpqF7/Xv3fnk399L9tsFF9JJDmi89QxPUbTzCp9tZJm331l\n" +
            "z0vv3vIvDr0L6/lkcnW7CF101TMke39o++y7iXNhZhrhJeQiYpALA4nBXlqIDSfl5DNuqRLi78cI\n" +
            "TeTenyjsfH+O4uZ87mlCYxgdCDkd36Y7tt9/GVJ76j2+3v+FcQ30uLOMUwuNQvMjxidcp3xdHd5z\n" +
            "dJjYZd4eun0OP41h4H2h7hoeiIfYgeUd+nmirSErKRYIlWeT7gTvId0J0eNi7ZJH2DPND36bAgMB\n" +
            "AAGjggF9MIIBeTAJBgNVHRMEAjAAMBEGCWCGSAGG+EIBAQQEAwIGQDBABglghkgBhvhCAQ0EMxYx\n" +
            "U2VydmVyIGNlcnRpZmljYXRlIGdlbmVyYXRlZCBmb3IgSlJDIENJU0UgVGVzdGJlZDAdBgNVHQ4E\n" +
            "FgQUNnu3RRLUuAxHJB/Gbm6ZyQ9kqbQwgdIGA1UdIwSByjCBx4AUZBhvJRkCRpYQbTknrZCa+HfR\n" +
            "pQOhgaqkgacwgaQxGzAZBgNVBAMMEmV1LmNpc2UuZXMgcm9vdCBDQTEUMBIGA1UECwwLSlJDIFRl\n" +
            "c3RCZWQxGzAZBgNVBAsMEmV1LmNpc2UuZXMgcm9vdCBDQTEbMBkGA1UECgwSZXUuY2lzZS5lcyBy\n" +
            "b290IENBMRIwEAYKCZImiZPyLGQBGRYCZXUxFDASBgoJkiaJk/IsZAEZFgRjaXNlMQswCQYDVQQG\n" +
            "EwJlc4ICEAAwDgYDVR0PAQH/BAQDAgWgMBMGA1UdJQQMMAoGCCsGAQUFBwMBMA0GCSqGSIb3DQEB\n" +
            "CwUAA4IBAQAxHpMj44EWLi1mP7OUMltCph4oW9SVEsRCeBpccZ7zFtJwM60wq5ZKj1zywECYY/y1\n" +
            "bGs/pXJvQljsMng2HdDjC5YaiWlcyRwfcKXrp96xa/0IMaWnKhU0lqz3lVLl7XXUz7SYCFTlgm3c\n" +
            "yfxgbBNIkIaDlA13zBs6hShKFxkAdsvX42zbeKm5fI186ztA/6gJ9XdQvzp11xgnI962+BonHnBZ\n" +
            "GnUznUWfqkRgLiQJ/hkPwHyERGD3em3GA4nrUhhmlqONy+45TVCcloQQL532gv1aQzWusvx5aCpt\n" +
            "6rdPV3EBEVAkVFId5jCLBadEYIt5IW1qQsoucoi4abDq0m5x";

    @Test
    public void when_xxx() throws CertificateException {
        X509Certificate certificate =
                parseBase64Certificate(
                        addBeginEndToCertificate(
                                removeCarriageReturn(message)));

        System.out.println(certificate.toString());

    }


    private X509Certificate parseBase64Certificate(String certText) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certText.getBytes(UTF_8)));
    }

    private String removeCarriageReturn(String text) {
        return text.replace("\n", "");
    }

    private String addBeginEndToCertificate(String certBase64) {
        return "-----BEGIN CERTIFICATE-----\n" + certBase64 + "\n-----END CERTIFICATE-----";
    }

}
