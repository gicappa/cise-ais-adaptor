package eu.cise.adaptor.context;

import eu.cise.adaptor.*;
import eu.cise.adaptor.dispatch.CatchingDispatcher;
import eu.cise.adaptor.sources.AisFileStreamGenerator;
import eu.cise.adaptor.translate.StringFluxToAisMsgFlux;
import eu.cise.adaptor.signature.DefaultCertificateRegistry;
import eu.cise.adaptor.signature.DefaultSignatureService;
import eu.cise.adaptor.signature.SignatureDispatcherDecorator;
import eu.cise.adaptor.translate.AisMsgToCiseModel;
import eu.cise.adaptor.translate.CiseModelToCiseMessage;

/**
 *
 */
public class DefaultAppContext implements AppContext {

    private final CertificateConfig config;

    public DefaultAppContext(CertificateConfig config) {
        this.config = config;
    }

    @Override
    public AisStreamGenerator makeSource() {
        return new AisFileStreamGenerator();
    }

    @Override
    public AisStreamProcessor makeStreamProcessor() {
        return new AisStreamProcessor(
                new StringFluxToAisMsgFlux(),
                new AisMsgToCiseModel(config),
                new CiseModelToCiseMessage(config),
                config);
    }

    @Override
    public Dispatcher makeDispatcher() {
        return new SignatureDispatcherDecorator(makeRestDispatcher(), makeSignatureService());
    }

    private SignatureService makeSignatureService() {
        return new DefaultSignatureService(
                new PrivateKeyInfo(
                        config.getGatewayId(),
                        config.getPrivateKeyPassword()),
                makeCertificateRegistry());
    }

    private DefaultCertificateRegistry makeCertificateRegistry() {
        return new DefaultCertificateRegistry(
                new KeyStoreInfo(
                        config.getPrivateJKSName(),
                        config.getPrivateJKSPassword()),
                new KeyStoreInfo(
                        config.getPublicJKSName(),
                        config.getPublicJKSPassword()));
    }

    private Dispatcher makeRestDispatcher() {
        return new CatchingDispatcher(new RestDispatcher());
    }

}