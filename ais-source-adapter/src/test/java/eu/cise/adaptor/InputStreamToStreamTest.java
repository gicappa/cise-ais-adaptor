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

import eu.cise.adaptor.translate.utils.InputStreamToStream;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static co.unruly.matchers.StreamMatchers.contains;
import static eu.cise.adaptor.DelimiterType.KEEP;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertThat;

public class InputStreamToStreamTest {

    private String aisOne = "!AIVDM,1,1,,B,13P88o@02=OqlrHM6FATwCvf08=E,0*73";
    private String aisTwo = "!AIVDM,1,1,,A,13P88o@uB=Oqm2<M6EkTvkvp0@@b,0*44";
    private String aisThree = "!AIVDM,1,1,,A,13P88o@uB=OqmFPM6DSTukwB0<1G,0*7D";

    @Test
    public void it_translate_an_InputStream_to_a_Stream_of_Strings_CR() {
        ByteArrayInputStream is = getAisInputStream(getAisMessages("\n"));
        InputStreamToStream toStream = new InputStreamToStream(is);

        assertThat(toStream.stream(), contains(aisOne, aisTwo, aisThree));
    }

    @Test
    public void it_translate_an_InputStream_to_a_Stream_of_Strings_EXCLAMATION() {
        ByteArrayInputStream is = getAisInputStream(getAisMessages(""));
        InputStreamToStream toStream = new InputStreamToStream(is, "!", KEEP);

        assertThat(toStream.stream(), contains(aisOne, aisTwo, aisThree));
    }

    private String getAisMessages(String delimiter) {
        return aisOne + delimiter + aisTwo + delimiter + aisThree + delimiter;
    }

    private ByteArrayInputStream getAisInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(UTF_8));
    }
}
