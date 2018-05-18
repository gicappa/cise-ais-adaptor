package eu.cise.adaptor;

import eu.cise.adaptor.dispatch.Dispatcher;
import eu.cise.adaptor.normalize.AISNormalizer;
import eu.cise.adaptor.signature.DefaultCertificateRegistry;
import eu.cise.adaptor.signature.DefaultSignatureService;
import eu.cise.adaptor.signature.SignatureDispatcherDecorator;
import eu.cise.adaptor.signature.SignatureService;
import eu.cise.adaptor.tbs.FileAISSource;
import eu.cise.adaptor.tbs.TbsAISNormalizer;

public class DefaultAppContext implements AppContext {

    private final CertificateConfig config;

    public DefaultAppContext(CertificateConfig config) {
        this.config = config;
    }

    @Override
    public AISSource makeSource() {
        return new FileAISSource();
    }

    @Override
    public AISNormalizer makeNormalizer() {
        return new TbsAISNormalizer();
    }

    @Override
    public Dispatcher makeDispatcher() {
        return new SignatureDispatcherDecorator(makeRestDispatcher(), makeSignatureService());
    }

    private SignatureService makeSignatureService() {
        return new DefaultSignatureService(makeCertificateRegistry());
    }

    private DefaultCertificateRegistry makeCertificateRegistry() {
        return new DefaultCertificateRegistry(
                config.getGatewayId(),
                config.getPrivateJKSName(),
                config.getPrivateJKSPassword(),
                config.getPrivateKeyPassword(),
                config.getPublicJKSName(),
                config.getPublicJKSPassword());
    }

    private RestDispatcher makeRestDispatcher() {
        return new RestDispatcher();
    }

}