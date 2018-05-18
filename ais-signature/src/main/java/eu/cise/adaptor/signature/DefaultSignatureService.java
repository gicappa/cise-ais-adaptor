package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.servicemodel.v1.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import sun.security.x509.X500Name;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.LogManager;

public class DefaultSignatureService implements SignatureService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DefaultCertificateRegistry registry;
    private final SignatureDelegate signatureDelegate;
    private XPathExpression certXPath;


    public DefaultSignatureService(DefaultCertificateRegistry registry) {
//      initJavaUtilLogging();  this should be used if unexpected behaviour appears in the XML SIG

        this.registry = registry;
        signatureDelegate = new SignatureDelegate(registry.findPrivateCertificate(), registry.findPrivateKey());
        initCertificateExtractionXPath();
    }

    private void initCertificateExtractionXPath() {
        try {
            certXPath = XPathFactory.newInstance().newXPath().compile("//*[local-name() = 'KeyInfo']/*[local-name() = 'X509Data']/*[local-name() = 'X509Certificate']");
        } catch (XPathExpressionException e) {
            throw new AISAdaptorException(e);
        }
    }


    private void initJavaUtilLogging() {
        InputStream configFile = this.getClass().getResourceAsStream("/logging.properties");
        if (configFile != null) {
            try {
                LogManager.getLogManager().readConfiguration(configFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void verifySignature(Message message) {
        signatureDelegate.verifySignatureWithMessageCertificate(message);
        verifyCertificateAgainstCACert(message);
    }


    @Override
    public Message sign(Message message) {
        return signatureDelegate.signMessageWithDelegatesPrivateKey(message);
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

            X509Certificate caCert = this.registry.findPublicCertificate(issuerCertNameInJKS);

            certificate.verify(caCert.getPublicKey());
        } catch (XPathExpressionException | IOException | CertificateException | NoSuchAlgorithmException |
                SignatureException | NoSuchProviderException | InvalidKeyException e) {
            throw new AISAdaptorException("Exception at certificate verification for message with ID {" + message.getMessageID() + "}", e);
        }
    }

}
