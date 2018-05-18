package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.servicemodel.v1.message.Message;
import org.w3c.dom.Element;
import sun.security.x509.X500Name;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static eu.cise.adaptor.signature.ExceptionHandler.safe;

public class DefaultSignatureService implements SignatureService {

    public static final String X_PATH_TO_CERTIFICATE = "//*[local-name() = 'KeyInfo']/*[local-name() = 'X509Data']/*[local-name() = 'X509Certificate']";
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
            Element certEl = (Element) certXPath.evaluate(message.getAny(), XPathConstants.NODE);
            String certBase64 = certEl.getFirstChild().getNodeValue().replace("\n", "");
            String certText = "-----BEGIN CERTIFICATE-----\n" + certBase64 + "\n-----END CERTIFICATE-----";
            X509Certificate certificate = (X509Certificate)
                    CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certText.getBytes("UTF-8")));

            String issuerCertNameInJKS = ((X500Name) certificate.getIssuerDN()).getOrganization()
                    .replace("eu.cise.", "").replace(' ', '-')
                    .toLowerCase() + ".cert";

            X509Certificate caCert = registry.findPublicCertificate(issuerCertNameInJKS);

            certificate.verify(caCert.getPublicKey());
        } catch (XPathExpressionException | IOException | CertificateException | NoSuchAlgorithmException |
                SignatureException | NoSuchProviderException | InvalidKeyException e) {
            throw new AISAdaptorException("Exception at certificate verification for message with ID {" + message.getMessageID() + "}", e);
        }
    }

}
