package eu.cise.adaptor.translate.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Stream;

public class InputStreamToStream {

    public Stream<String> stream(InputStream is) {
        return new BufferedReader(new InputStreamReader(is, Charset.defaultCharset())).lines();
    }

}
