/*
 * Copyright CISE AIS Adaptor (c) 2018, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
