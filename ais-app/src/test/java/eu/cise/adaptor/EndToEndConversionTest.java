package eu.cise.adaptor;

import eu.cise.adaptor.server.TestRestServer;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndToEndConversionTest {

    private CertificateConfig config;
    private TestRestServer testRestServer;
    private MainApp app;

    @Before
    public void before() {
        config = ConfigFactory.create(CertificateConfig.class);
        testRestServer = new TestRestServer(64738, 10);
        new Thread(testRestServer).start();
        app = new MainApp(config);
    }

    @Test
    public void it_deserialize_a_message_from_a_file() {
        app.run();

        assertEquals(96, testRestServer.countInvocations());
    }

    @After
    public void after() {
        testRestServer.shutdown();
    }
}
