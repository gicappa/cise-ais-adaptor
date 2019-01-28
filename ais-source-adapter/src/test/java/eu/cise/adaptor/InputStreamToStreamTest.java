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
