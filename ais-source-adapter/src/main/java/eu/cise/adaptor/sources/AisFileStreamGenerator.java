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

package eu.cise.adaptor.sources;

import eu.cise.adaptor.AisStreamGenerator;
import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.translate.utils.InputStreamToStream;
import org.aeonbits.owner.ConfigFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * This stream generator opens a file and reads it line by line interpreting the
 * AIS message information in a textual format. Each message is separated by the
 * others through a line feed (LF) character and as soon as is read is sent to
 * the {@link java.util.stream.Stream} of {@link String}.
 */
public class AisFileStreamGenerator implements AisStreamGenerator {

    private final AisFileAdaptorConfig config;

    /**
     * Construct the stream generator by reading a file specified in the
     * configuration file.
     * <p>
     * the property {@code ais-source.file.name} specifies the file name that
     * could be found in the filesystem or in the classpath.
     */
    public AisFileStreamGenerator() {
        config = ConfigFactory.create(AisFileAdaptorConfig.class);

        if (config.getAISSourceFilename() == null)
            throw new AdaptorException("The 'ais-source.file.name' property is not " +
                                               "set in the ais-adaptor.properties file");
    }

    /**
     * The generate method tries to open an input stream from the file name
     * and returns a {@link java.util.stream.Stream} of strings with the
     * AIS message in every string.
     *
     * @return the Stream with the messages
     * @throws AdaptorException if the file does not exists
     */
    @Override
    public Stream<String> generate() {
        InputStream inputStream = open(config.getAISSourceFilename());

        if (inputStream == null) {
            throw new AdaptorException(
                    "The file '" + config.getAISSourceFilename() +
                            "' does not exists neither in the /conf/" +
                            " directory nor in the classpath");
        }

        return new InputStreamToStream(inputStream).stream();
    }

    /**
     * It checks if there is a resource matching the file name and returns the
     * {@link java.io.InputStream} with the data.
     * Otherwise it checks for a file in the filesystem with that name and
     * returns the {@link java.io.InputStream} with the data.
     *
     * @param filename is the filename to be opened
     * @return the {@link java.io.InputStream} or null if it does not find the file.
     */
    public InputStream open(String filename) {
        InputStream is = openResource(filename);

        if (is != null)
            return is;

        return openFile(filename);
    }

    /**
     * @param filename file name to be opened from the classpath.
     * @return the {@link java.io.InputStream} from the resource on null otherwise.
     */
    public InputStream openResource(String filename) {
        return AisFileStreamGenerator.class.getClassLoader().getResourceAsStream(filename);
    }

    /**
     * @param filename file name to be opened from the file system.
     * @return the {@link java.io.InputStream} from the resource on null otherwise.
     */
    public InputStream openFile(String filename) {
        try {
            return new FileInputStream(System.getProperty("conf.dir") + filename);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
