package eu.cise.adaptor;

import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ConfigPrinterTest {

    private String configOuput;

    @Before
    public void before() {
        CertificateConfig config = ConfigFactory.create(CertificateConfig.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(baos);
        ConfigPrinter printer = new ConfigPrinter(config, output);

        printer.print();

        configOuput = new String(baos.toByteArray(), UTF_8);
    }

    @Test
    public void it_should_avoid_printing_passwords() {
        assertThat(configOuput, not(containsString("cisecise")));
    }

    @Test
    public void it_should_print_stars_instead() {
        assertThat(configOuput, containsString("=********"));
    }
}
