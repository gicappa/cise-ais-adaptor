package eu.cise.adaptor;

import eu.cise.adaptor.exceptions.AdaptorException;
import eu.cise.adaptor.server.TcpServerRule;
import eu.cise.adaptor.sources.AuthTcpStreamGenerator;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.fail;

public class AuthTcpStreamGeneratorTest {

    public @Rule
    TcpServerRule serverRule = new TcpServerRule();

    @Test
    public void it_waits_for_the_first_ais_message_in_the_stream() {
        try {
            AisStreamGenerator streamGenerator
                    = new AuthTcpStreamGenerator("localhost", 64738, new Socket());

            streamGenerator.generate().forEach(ais -> System.out.println("< OK"));

        } catch (Exception e) {
            fail("something went wrong with the protocol");
        }
    }

    @Test(expected = AdaptorException.class)
    @Ignore
    public void it_exits_if_the_login_string_is_wrong_or_missing() {
        serverRule.getWorkerFactory().setAuthString("AUTH=wrong:wrong");

        AisStreamGenerator streamGenerator
                = new AuthTcpStreamGenerator("localhost", 64738, new Socket());

        streamGenerator.generate();
    }

}
