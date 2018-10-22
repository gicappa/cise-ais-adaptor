package eu.cise.adaptor.signature;

import eu.cise.adaptor.exceptions.AdaptorException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResourceFileTest {

    private static final String RESOURCE_FILE_NAME = "resource-file-test.txt";
    private ResourceFile resourceFile;
    private File tmpResourceFile;
    private File fsResourceFile;

    @Before
    public void before() throws IOException {
        System.setProperty("conf.dir", tmpDir());

        tmpResourceFile = File.createTempFile("resource-file", null);
        Files.write(Paths.get(filePath()), "filesystem".getBytes());
        fsResourceFile = new File(filePath());

    }

    @After
    public void after() {
        Stream.of(tmpResourceFile, fsResourceFile)
                .map(File::delete)
                .forEach(res -> {
                    if (!res)
                        System.err.println("WARN: Some test file not deleted from temp dir.");
                });
    }

    @Test
    public void it_opens_inputStream_in_resources() {
        resourceFile = new ResourceFile(RESOURCE_FILE_NAME);

        assertNotNull(resourceFile.getStream());
    }

    @Test(expected = AdaptorException.class)
    public void it_throws_exception_when_not_existing() {
        resourceFile = new ResourceFile("not-existing-file.txt");

        resourceFile.getStream();
    }

    @Test
    public void it_opens_inputStream_in_fileSystem() {
        resourceFile = new ResourceFile(tmpResourceFile.getName());

        assertNotNull(resourceFile.getStream());
    }

    @Test
    public void it_select_filesystem_over_resources() throws IOException {
        resourceFile = new ResourceFile("resource-file-test.txt");

        assertEquals(readContent(resourceFile.getStream()), "filesystem");
    }

    private String readContent(InputStream stream) throws IOException {
        return new String(IOUtils.readFully(stream, -1, true), UTF_8);
    }

    private String filePath() {
        return tmpDir() + File.separator + "resource-file-test.txt";
    }

    private String tmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

}
