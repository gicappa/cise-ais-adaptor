package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISAdaptorConfig;
import org.aeonbits.owner.Config;

@Config.Sources("classpath:ais-adaptor.properties")
public interface FileAISSourceConfig extends AISAdaptorConfig {

    @Key("ais-source.file.name")
    String getAISSourceFilename();

}
