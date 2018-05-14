package eu.cise.adaptor.tbs;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * The string format of the eta is "18-07 17:00"
 * ETA:
 * Estimated time of arrival; MMDDHHMM UTC
 * Bits 19-16: month; 1-12; 0 = not available = default
 * Bits 15-11: day; 1-31; 0 = not available = default
 * Bits 10-6: hour; 0-23; 24 = not available = default
 * Bits 5-0: minute; 0-59; 60 = not available = default
 */
public class Eta {
    private final Clock clock;

    public Eta(Clock clock) {
        this.clock = clock;
    }

    Instant computeETA(String etaString) {
        if (etaString == null) return null;

        Instant eta = Instant.parse(getDateTime(etaString));

        if (eta.isBefore(Instant.now(clock)) && eta.isAfter(Instant.parse("1971-01-01T00:00:00Z")))
            eta = eta.plus(365, ChronoUnit.DAYS);

        return eta;
    }

    private String getDefaultMonth(String eta, String defaultValue) {
        return getMonth(eta).equals("00") ? defaultValue : getMonth(eta);
    }

    private String getDefaultDay(String eta, String defaultValue) {
        return getMonth(eta).equals("00") ? defaultValue : getDay(eta);
    }

    private String getDefaultHours(String eta, String defaultValue) {
        return getHours(eta).equals("24") ? defaultValue : getHours(eta);
    }

    private String getDefaultMinutes(String eta, String defaultValue) {
        return getMinutes(eta).equals("60") ? defaultValue : getMinutes(eta);
    }

    private String getDateTime(String eta) {
        return getDate(eta) + "T" + getTime(eta);
    }

    private String getTime(String eta) {
        return getDefaultHours(eta, "00") + ":" + getDefaultMinutes(eta, "00") + ":00.000Z";
    }

    private String getHoursColumnMinutes(String etaStr) {
        return etaStr.split(" ")[1];
    }

    private String getHours(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[0];
    }

    private String getMinutes(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[1];
    }

    private String getDate(String eta) {
        return getCurrentYear(eta) + "-" +
                getDefaultMonth(eta, "01") + "-" +
                getDefaultDay(eta, "01");
    }

    private String getDay(String eta) {
        return getMonthDashDay(eta).split("-")[0];
    }

    private String getMonth(String eta) {
        return getMonthDashDay(eta).split("-")[1];
    }

    private String getMonthDashDay(String eta) {
        return eta.split(" ")[0];
    }

    private int getCurrentYear(String etaStr) {
        if (getDay(etaStr).equals("00") || getMonth(etaStr).equals("00"))
            return 1970;

        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).getYear();
    }

}