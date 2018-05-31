package eu.cise.adaptor;

import org.aeonbits.owner.ConfigFactory;
import org.junit.Test;

public class MainAppIdeLauncher {

    @Test
    public void run() {
        new MainApp(ConfigFactory.create(CertificateConfig.class)).run();
    }
}
