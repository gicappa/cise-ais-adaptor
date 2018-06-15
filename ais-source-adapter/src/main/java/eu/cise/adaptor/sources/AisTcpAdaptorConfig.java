package eu.cise.adaptor.sources;

import eu.cise.adaptor.AdaptorConfig;
import org.aeonbits.owner.Config;

/**
 * Configuration file for the tcp stream generator
 */
@SuppressWarnings("unused")
@Config.Sources({"file:${conf.dir}ais-adaptor.properties",
        "classpath:ais-adaptor.properties"})
public interface AisTcpAdaptorConfig extends AdaptorConfig {

    @Key("ais-source.socket.host")
    String getAISSourceSocketHost();

    @Key("ais-source.socket.port")
    Integer getAISSourceSocketPort();
}
