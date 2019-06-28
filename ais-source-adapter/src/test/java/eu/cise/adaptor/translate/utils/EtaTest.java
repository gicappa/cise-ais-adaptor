package eu.cise.adaptor.translate.utils;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.nullValue;
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
 * TODAY: 08/01/2019, ETA: 10/12 => 10/12/2018
 * TODAY: 10/12/2018, ETA: 08/01 => 08/01/2019
 * TODAY: 07/06/2019, ETA: 07/04 => 07/04/2019
 * TODAY: 07/04/2019, ETA: 07/06 => 07/06/2019
 */
public class EtaTest {

    private Eta eta;
    private EtaDate etaDate;
    private EtaTime etaTime;

    @Before
    public void before() {
        etaDate = new EtaDate();
        etaTime = new EtaTime();
    }

    @Test
    public void ETA_is_null_when_the_day_is_unavailable() {
        assertThat(etaDate.getMonthDayISOFormat("0-12 17:00"), is(nullValue()));
    }

    @Test
    public void ETA_is_null_when_the_month_is_unavailable() {
        assertThat(etaDate.getMonthDayISOFormat("10-0 17:00"), is(nullValue()));
    }

    @Test
    public void compute_ETA_is_null_when_the_month_is_unavailable() {
        eta = new Eta(clockAt("2019-01-08T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("0-12 17:00"), is(nullValue()));
    }

    @Test
    public void compute_ETA_is_null_when_the_day_is_unavailable() {
        eta = new Eta(clockAt("2019-01-08T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("10-0 17:00"), is(nullValue()));
    }

    @Test
    public void compute_ETA_defaults_the_hour_when_unavailable() {
        eta = new Eta(clockAt("2019-01-08T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("09-01 24:00"), is(Instant.parse("2019-01-09T00:00:00.00Z")));
    }

    @Test
    public void compute_ETA_defaults_the_minute_when_unavailable() {
        eta = new Eta(clockAt("2019-01-08T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("09-01 15:60"), is(Instant.parse("2019-01-09T15:00:00.00Z")));
    }

    @Test
    public void compute_ETA_is_before_today_with_new_year_in_the_middle() {
        eta = new Eta(clockAt("2019-01-08T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("10-12 17:00"), is(Instant.parse("2018-12-10T17:00:00.00Z")));
    }

    @Test
    public void compute_ETA_is_after_today_with_new_year_in_the_middle() {
        eta = new Eta(clockAt("2018-12-10T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("08-01 17:00"), is(Instant.parse("2019-01-08T17:00:00.00Z")));
    }

    @Test
    public void compute_ETA_is_before_today_without_new_year_in_the_middle() {
        eta = new Eta(clockAt("2019-07-06T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("07-04 17:00"), is(Instant.parse("2019-04-07T17:00:00.00Z")));
    }

    @Test
    public void compute_today_is_before_ETA_without_new_year_in_the_middle() {
        eta = new Eta(clockAt("2019-07-04T17:00:00.00Z"), etaDate, etaTime);
        assertThat(eta.computeETA("07-06 17:00"), is(Instant.parse("2019-06-07T17:00:00.00Z")));
    }

    private Clock clockAt(String date) {
        return Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
    }
}
