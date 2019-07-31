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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static java.lang.Math.abs;
import static org.apache.commons.lang3.ObjectUtils.min;

/**
 * The string format of the eta is "18-07 17:00"
 * ETA:
 * Estimated time of arrival; MMDDHHMM UTC
 * Bits 19-16: month; 1-12; 0 = not available = default
 * Bits 15-11: day; 1-31; 0 = not available = default
 * Bits 10-6: hour; 0-23; 24 = not available = default
 * Bits 5-0: minute; 0-59; 60 = not available = default
 * <p>
 * To infer the YEAR of the ETA that is not specified the following
 * algorithm will be implemented.
 * Three dates will be created user ETA Month-Day and:
 * - current year
 * - next year
 * - previous year
 * The current date will be compared with each of these dates and
 * the closest ETA (shortest period of time in absolute terms) will
 * be selected.
 * <p>
 * Requirements:
 * If the ETA Month or Day are not valid: the ETA should be null
 * If the ETA Hour  or Minute are not valid: the only Date part should be taken into account
 */
public class Eta {
    private final Clock clock;
    private final EtaDate etaDate;
    private final EtaTime etaTime;

    public Eta(Clock clock, EtaDate etaDate, EtaTime etaTime) {
        this.clock = clock;
        this.etaDate = etaDate;
        this.etaTime = etaTime;
    }

    /**
     * Will translate the eta from AIS format to an ISO one.
     * When in doubt will chose the ETA year with the minor time difference
     * from today.
     *
     * @param etaStr the ETA string in the format "13-04 15:39"
     * @return an Instant with the corresponding date time
     */
    public Instant computeETA(String etaStr) {
        if (etaStr == null)
            return null;

        // returns null in case of unspecified month or day
        if (etaDate.getMonthDayISOFormat(etaStr) == null)
            return null;

        // calculating three eta for the current year
        // previous and next: the closest to the time of
        // today will be selected.
        Instant etaThisYear = Instant.parse(getDateTime(getCurrentYear(), etaStr));
        Instant etaNextYear = Instant.parse(getDateTime(getCurrentYear() + 1, etaStr));
        Instant etaPrevYear = Instant.parse(getDateTime(getCurrentYear() - 1, etaStr));

        long diffThisYear = abs(ChronoUnit.DAYS.between(Instant.now(clock), etaThisYear));
        long diffNextYear = abs(ChronoUnit.DAYS.between(Instant.now(clock), etaNextYear));
        long diffPrevYear = abs(ChronoUnit.DAYS.between(Instant.now(clock), etaPrevYear));

        // compute the minimum of the three time difference
        long diffMin = min(diffThisYear, min(diffNextYear, diffPrevYear));

        // returns the ETA with the year of the minimum
        // time difference from today
        if (diffMin == diffThisYear)
            return etaThisYear;
        else if (diffMin == diffNextYear)
            return etaNextYear;
        else
            return etaPrevYear;
    }


    private String getDateTime(int year, String eta) {
        return year + "-" + etaDate.getMonthDayISOFormat(eta) + "T" + etaTime.getTime(eta);
    }

    private int getCurrentYear() {
        return LocalDateTime.ofInstant(Instant.now(clock), ZoneOffset.UTC).getYear();
    }

}
