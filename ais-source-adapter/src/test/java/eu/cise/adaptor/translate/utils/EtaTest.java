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

package eu.cise.adaptor.translate.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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

    @BeforeEach
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
