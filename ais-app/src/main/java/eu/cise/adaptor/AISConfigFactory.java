package eu.cise.adaptor;

import org.aeonbits.owner.ConfigFactory;

public class AISConfigFactory {

    private final AISAdaptorConfig aisAdaptorConfig;

    public AISConfigFactory() {
        aisAdaptorConfig = ConfigFactory.create(AISAdaptorConfig.class);
    }

    public AISAdaptorConfig create() {
        return aisAdaptorConfig;
    }

}
