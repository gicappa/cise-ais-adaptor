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
        System.out.println("### Printing Configuration ###\n");
        System.out.println("# Connection settings");
        printProperty("gateway.address: ", config.getGatewayAddress());
        printProperty("processing.idle.time: ", config.getProcessingIdleTime());
        printProperty("processing.entities-per-message: ", config.getNumberOfEntitiesPerMessage());
        printProperty("demo-environment: ", config.isDemoEnvironment());
        printProperty("override-timestamps: ", config.isOverridingTimestamps());
        System.out.println();

        System.out.println("# Recipient");
        printProperty("recipient.service.id: ", config.getRecipientServiceId());
        printProperty("recipient.service.operation: ", config.getRecipientServiceOperation());
        System.out.println();

        System.out.println("# Sender");
        printProperty("sender.service.id: ", config.getServiceId());
        printProperty("sender.service.operation: ", config.getServiceOperation());
        printProperty("sender.service.participant.url: ", config.getEndpointUrl());
        System.out.println();

        System.out.println("# Service Profile");
        printProperty("sender.service.data-freshness-type: ", config.getDataFreshnessType());
        printProperty("sender.service.sea-basin-type: ", config.getSeaBasinType());
        System.out.println();

        System.out.println("# Payload properties");
        printProperty("message.priority: ", config.getMessagePriority());
        printProperty("message.purpose: ", config.getPurpose());
        printProperty("message.security-level: ", config.getSecurityLevel());
        printProperty("message.sensitivity: ", config.getSensitivity());
        System.out.println();
        System.out.println("##############################\n");
    }

    private void printProperty(String key, Object value) {
        System.out.print(key);
        System.out.println(value);
    }

}
