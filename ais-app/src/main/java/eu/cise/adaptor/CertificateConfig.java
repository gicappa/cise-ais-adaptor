package eu.cise.adaptor;


import static org.aeonbits.owner.Config.Sources;

@SuppressWarnings("unused")
@Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface CertificateConfig extends AISAdaptorConfig {

    @DefaultValue("eu.cise.es.gc-ls01")
    String getGatewayId();

    @DefaultValue("cisePrivate.jks")
    String getPrivateJKSName();

    @DefaultValue("cisecise")
    String getPrivateJKSPassword();

    @DefaultValue("cisecise")
    String getPrivateKeyPassword();

    @DefaultValue("cisePublic.jks")
    String getPublicJKSName();

    @DefaultValue("cisecise")
    String getPublicJKSPassword();
}
