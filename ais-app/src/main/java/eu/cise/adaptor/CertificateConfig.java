package eu.cise.adaptor;


import static org.aeonbits.owner.Config.Sources;

/**
 * Extending the {AdaptorConfig} configuration object adding properties
 * specific to the Certificate and Signature
 */
@SuppressWarnings("unused")
@Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface CertificateConfig extends AdaptorConfig {

    @DefaultValue("eu.cise.es.gc-ls01")
    @Key("adaptor.id")
    String getAdaptorId();

    @DefaultValue("cisePrivate.jks")
    @Key("signature.private.jks.filename")
    String getPrivateJKSName();

    @DefaultValue("cisecise")
    @Key("signature.private.jks.password")
    String getPrivateJKSPassword();

    @DefaultValue("cisecise")
    @Key("signature.private.key.password")
    String getPrivateKeyPassword();

    @DefaultValue("cisePublic.jks")
    @Key("signature.public.jks.filename")
    String getPublicJKSName();

    @DefaultValue("cisecise")
    @Key("signature.public.jks.password")
    String getPublicJKSPassword();
}
