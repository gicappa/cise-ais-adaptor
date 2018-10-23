package eu.cise.adaptor.sign11;

import eu.cise.adaptor.CertificateRegistry;
import eu.cise.adaptor.PrivateKeyInfo;
import eu.cise.adaptor.SignatureService;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.servicemodel.v1.message.Message;
import org.w3c.dom.Element;
import sun.security.x509.X500Name;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static eu.cise.adaptor.exceptions.ExceptionHandler.safe;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.xml.xpath.XPathConstants.NODE;

public class DefaultSignatureService implements SignatureService {

    public static final String X_PATH_TO_CERTIFICATE
            = "//*[local-name() = 'KeyInfo']/*[local-name() = 'X509Data']/*[local-name() = " +
            "'X509Certificate']";
    private final CertificateRegistry registry;
    private final SignatureDelegate signature;
    private XPathExpression certXPath;

    public DefaultSignatureService(PrivateKeyInfo myPrivateKey, CertificateRegistry registry) {
        this.registry = registry;

        signature = new SignatureDelegate(
                registry.findPrivateCertificate(myPrivateKey.keyAlias()),
                registry.findPrivateKey(myPrivateKey.keyAlias(), myPrivateKey.password()));

        certXPath = compileXPath(X_PATH_TO_CERTIFICATE);
    }

    private XPathExpression compileXPath(String xPathCertificate) {
        return safe(() -> XPathFactory.newInstance().newXPath().compile(xPathCertificate));
    }

    @Override
    public void verify(Message message) {
        signature.verifySignatureWithMessageCertificate(message);
        verifyCertificateAgainstCACert(message);
    }


    @Override
    public Message sign(Message message) {
        return signature.signMessageWithDelegatesPrivateKey(message);
    }

    private void verifyCertificateAgainstCACert(Message message) {
        try {
            X509Certificate certificate = parseBase64Certificate(
                    addBeginEndToCertificate(
                            removeCarriageReturn(
                                    extractCertificateText(
                                            getCertificateElement(message)))));

            String issuerCertNameInJKS = extractIssuerNameFrom(certificate);

            X509Certificate caCert = registry.findPublicCertificate(issuerCertNameInJKS);

            certificate.verify(caCert.getPublicKey());

        } catch (XPathExpressionException | IOException | CertificateException | NoSuchAlgorithmException |
                SignatureException | NoSuchProviderException | InvalidKeyException e) {
            throw new AdaptorException("Exception at certificate verification for message with ID" +
                                               " {" + message.getMessageID() + "}", e);
        }
    }

    private String extractIssuerNameFrom(X509Certificate certificate) throws IOException {
        return ((X500Name) certificate.getIssuerDN())
                .getOrganization().replace("eu.cise.", "")
                .replace(' ', '-')
                .toLowerCase() + ".cert";
    }

    private X509Certificate parseBase64Certificate(String certText) throws CertificateException,
                                                                           UnsupportedEncodingException {
        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certText.getBytes(UTF_8)));
    }

    private String removeCarriageReturn(String text) {
        return text.replace("\n", "");
    }

    private String extractCertificateText(Element certificateElement) {
        return certificateElement.getFirstChild().getNodeValue();
    }

    private Element getCertificateElement(Message message) throws XPathExpressionException {
        return (Element) certXPath.evaluate(message.getAny(), NODE);
    }

    private String addBeginEndToCertificate(String certBase64) {
        return "-----BEGIN CERTIFICATE-----\n" + certBase64 + "\n-----END CERTIFICATE-----";
    }

}
