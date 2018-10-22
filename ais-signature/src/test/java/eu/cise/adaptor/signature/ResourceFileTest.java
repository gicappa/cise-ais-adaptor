package eu.cise.adaptor.signature;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class ResourceFileTest {

    private ResourceFile resourceFile;

    @Before
    public void before() {
//        new PrivateKeyInfo("eu.cise.es.gc-ls01", "cisecise");
//        new KeyStoreInfo("cisePrivate.jks", "cisecise");
//        new KeyStoreInfo("cisePublic.jks", "cisecise");
//
//        new PrivateKeyInfo("eu.cise.es.gc-ls01", "cisecise");
//        new KeyStoreInfo("cisePrivate.jks", "cisecise");
//        new KeyStoreInfo("cisePublic.jks", "cisecise");
    }

    @Test
    public void open_inputStream_in_resources() {
        resourceFile = new ResourceFile("resource-file-test.txt");

        assertNotNull(resourceFile.getStream());
    }
}
