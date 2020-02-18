/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
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

package eu.cise.adaptor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Pretty printing configuration
 */
class ConfigPrinter {

    private final AdaptorConfig config;
    private final PrintStream outStream;

    ConfigPrinter(AdaptorConfig config) {
        this(config, System.out);
    }

    ConfigPrinter(AdaptorConfig config, PrintStream out) {
        this.config = config;
        this.outStream = out;
    }

    void print() {
        outStream.println("### Printing Configuration ###");

        stringToStream(configToString(config))
                .map(this::replacePassword)
                .forEach(outStream::println);

        outStream.println("##############################");
    }

    private String replacePassword(String s) {
        return s.replaceAll("(.*password.*)=(.*)", "$1=********");
    }

    private Stream<String> stringToStream(String output) {
        return new BufferedReader(new StringReader(output)).lines();
    }

    private String configToString(AdaptorConfig config) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream buffered = new PrintStream(baos);
        config.list(buffered);
        return new String(baos.toByteArray(), UTF_8);
    }

}
