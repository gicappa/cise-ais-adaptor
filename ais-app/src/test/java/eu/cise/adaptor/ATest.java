package eu.cise.adaptor;

import eu.cise.adaptor.server.TestRestServer;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ATest {

    private Thread threadMainApp;
    private TestRestServer testRestServer;

    @Before
    public void before() {
        System.setProperty("ais-source.file.name", "example.ais.stream.txt");
        System.setProperty("gateway.address", "localhost:64738");

        CertificateConfig config = ConfigFactory.create(CertificateConfig.class, System.getProperties());

        testRestServer = new TestRestServer(64738, 10);
        new Thread(testRestServer).start();
        threadMainApp = new Thread(new MainApp(config));


    }

    @Test
    public void it_deserialize_a_message_from_a_file() {
        try {
            threadMainApp.start();
            Thread.sleep(4);
            System.out.println("------------> " + testRestServer.countInvocations());
        } catch (InterruptedException e) {
            testRestServer.shutdown();
            threadMainApp.stop();

        }

    }

    @After
    public void after() {
        testRestServer.shutdown();
        threadMainApp.stop();
    }
}
