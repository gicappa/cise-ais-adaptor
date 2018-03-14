package eu.cise.adaptor.tbs;

import eu.cise.adaptor.AISAdaptorConfig;

public interface SocketAISSourceConfig extends AISAdaptorConfig {
    @Key("ais-source.socket.host")
    String getAISSourceSocketHost();

    @Key("ais-source.socket.port")
    Integer getAISSourceSocketPort();
}
