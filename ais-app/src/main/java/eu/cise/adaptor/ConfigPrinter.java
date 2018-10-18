package eu.cise.adaptor;

/**
 * Pretty printing configuration
 */
public class ConfigPrinter {

    private AdaptorConfig config;

    public ConfigPrinter(AdaptorConfig config) {
        this.config = config;
    }

    public void print() {
        System.out.println("### Printing Configuration ###");
        config.list(System.out);
        System.out.println("##############################");
    }

}
