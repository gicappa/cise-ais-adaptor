package eu.cise.adaptor.context;

import eu.cise.adaptor.AppContext;
import eu.cise.adaptor.AdaptorExtConfig;
import eu.cise.adaptor.exceptions.AdaptorException;

/**
 * app-context.type=tcp
 * app-context.type=tcp-auth
 * app-context.type=file
 */
public class AppContextFactory {

    private final AdaptorExtConfig config;

    public AppContextFactory(AdaptorExtConfig config) {
        this.config = config;
    }

    public AppContext create() {
        if (config.getAppContextType() == null)
            throw new AdaptorException(errorMessage("null"));

        switch (config.getAppContextType()) {
            case "file":
                return new FileAppContext(config);
            case "tcp":
                return new TcpAppContext(config);
            case "auth-tcp":
                return new AuthTcpAppContext(config);
            default:
                throw new AdaptorException(errorMessage(config.getAppContextType()));
        }
    }

    private String errorMessage(String msg) {
        return "Invalid app-context.type property in ais-adaptor.properties file [" + msg + "]";
    }

}
