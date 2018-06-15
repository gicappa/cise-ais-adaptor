package eu.cise.adaptor.sources;

import eu.cise.adaptor.AdaptorConfig;
import org.aeonbits.owner.Config;

@SuppressWarnings("unused")
@Config.Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface AisFileAdaptorConfig extends AdaptorConfig {

    @Key("ais-source.file.name")
    String getAISSourceFilename();

}
