package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISAdaptorConfig;

public interface FileAISSourceConfig extends AISAdaptorConfig {

    @Key("ais-source.file.name")
    String getAISSourceFilename();

}
