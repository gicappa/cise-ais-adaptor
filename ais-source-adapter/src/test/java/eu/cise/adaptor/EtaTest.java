package eu.cise.adaptor;

import eu.cise.adaptor.translate.utils.Eta;
import eu.cise.adaptor.translate.utils.EtaParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * Recap of the rules:
 * ETA string format: "18-07 17:00"
 * <p>
 * Given:
 * D: day
 * M: Month
 * <p>
 * Examples:
 * <p>
 * CurrentDate=2019-06-27
 * <p>
 * TODAY: 08/01/2019, ETA: 10/12 => 10/12/2019
 * TODAY: 10/12/2018, ETA: 08/01 => 08/01/2019
 * TODAY: 07/06/2019, ETA: 07/04 => 07/04/2019
 * TODAY: 07/04/2019, ETA: 07/06 => 07/06/2019
 */
@Ignore
public class EtaTest {

    private Eta eta;
    private EtaParser parser;

    @Before
    public void before() {
        parser = new EtaParser();
    }

    @Test
    public void compute_ETA_is_before_today_with_new_year_in_the_middle() {
        eta = new Eta(clockAt("2019-01-08T17:00:00.00Z"), parser);
        assertThat(eta.computeETA("10-12 17:00"), is(Instant.parse("2019-12-10T17:00:00.00Z")));
    }

    @Test
    public void compute_today_is_before_ETA_with_new_year_in_the_middle() {
        eta = new Eta(clockAt("2018-12-10T17:00:00.00Z"), parser);
        assertThat(eta.computeETA("08-01 17:00"), is(Instant.parse("2019-01-08T17:00:00.00Z")));
    }

    @Test
    public void compute_ETA_is_before_today_without_new_year_in_the_middle() {
        eta = new Eta(clockAt("2019-07-06T17:00:00.00Z"), parser);
        assertThat(eta.computeETA("07-04 17:00"), is(Instant.parse("2019-04-07T17:00:00.00Z")));
    }

    @Test
    public void compute_today_is_before_ETA_without_new_year_in_the_middle() {
        eta = new Eta(clockAt("2019-07-04T17:00:00.00Z"), parser);
        assertThat(eta.computeETA("07-06 17:00"), is(Instant.parse("2019-06-07T17:00:00.00Z")));
    }

    private Clock clockAt(String date) {
        return Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
    }
}
