package eu.cise.adaptor.signature;
import eu.cise.adaptor.exceptions.AISAdaptorException;
import eu.cise.lib.signature.Sign;
import eu.cise.lib.signature.SignException;
import eu.cise.lib.signature.SignatureVerification;
import eu.cise.lib.signature.VerificationException;
import eu.cise.servicemodel.v1.message.Message;
import eu.eucise.xml.DefaultXmlMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.LogManager;

public class DefaultSignatureService implements SignatureService {

    private final CertificateRegistry certificateRegistry;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DefaultXmlMapper.NotValidating noValidationMapper = new DefaultXmlMapper.NotValidating();
    private final X509Certificate certificate;
    private final PrivateKey privateKey;
    private final XPathExpression certXPath;

    public DefaultSignatureService(CertificateRegistry certificateRegistry) {
//        initJavaUtilLogging();  this should be used if unexpected behaviour appears in the XML SIG

        Pair<Certificate[], PrivateKey> certPair = certificateRegistry.findPrivateKeyAndCertificateForCurrentGateway();
        this.certificate = (X509Certificate) certPair.getLeft()[0];
        this.privateKey = certPair.getRight();
        this.certificateRegistry = certificateRegistry;

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
        Document doc = noValidationMapper.toDOM(message);
        try {
            SignatureVerification.verifyWithCertificate(doc);

            verifyCertificateAgainstCACert(message);
        } catch (VerificationException e) {
            throw new AISAdaptorException("Error at signature verification for message with ID {" + message.getMessageID() + "}", e);
        }
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

            X509Certificate caCert = this.certificateRegistry.findPublicCertificate(issuerCertNameInJKS);

            certificate.verify(caCert.getPublicKey());
        } catch (XPathExpressionException | IOException | CertificateException | NoSuchAlgorithmException |
                java.security.SignatureException | NoSuchProviderException | InvalidKeyException e) {
            throw new AISAdaptorException("Exception at certificate verification for message with ID {" + message.getMessageID() + "}", e);
        }

    }

    @Override
    public Message sign(Message message) {
        Document unsignedDoc = noValidationMapper.toDOM(message);
        NodeList nl = unsignedDoc.getElementsByTagName("Signature");
        if (nl.getLength() > 0) {
            Element sigElement = (Element) nl.item(0);
            unsignedDoc.getDocumentElement().removeChild(sigElement);
        }
        Document signedDoc = signDoc(unsignedDoc);
        Message outMessage = noValidationMapper.fromDOM(signedDoc);
        return outMessage;
    }

    private Document signDoc(Document unsignedDoc) {
        try {
            return Sign.sign(certificate, privateKey, unsignedDoc);
        } catch (SignException e) {
            throw new RuntimeException(e);
        }
    }
}
