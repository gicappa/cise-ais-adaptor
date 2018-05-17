package eu.cise.adaptor;

import eu.cise.adaptor.server.TestRestServer;
import eu.eucise.xml.DefaultXmlMapper;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EndToEndConversionTest {

    private CertificateConfig config;
    private Thread threadMainApp;
    private TestRestServer testRestServer;
    private DefaultXmlMapper xmlMapper;

    @Before
    public void before() {
        config = ConfigFactory.create(CertificateConfig.class);
        testRestServer = new TestRestServer(64738, 10);
        new Thread(testRestServer).start();
        threadMainApp = new Thread(new MainApp(config));
        xmlMapper = new DefaultXmlMapper();
    }

    @Test
    public void it_deserialize_a_message_from_a_file() {
        try {
            threadMainApp.start();
            testRestServer.checkRequest(r-> xmlMapper.fromXML(r));
            threadMainApp.join(30000);

            assertEquals(96, testRestServer.countInvocations());
        } catch (InterruptedException e) {
            testRestServer.shutdown();
            fail("An exception occurred");
        }
    }

    @After
    public void after() {
        testRestServer.shutdown();
    }
}
