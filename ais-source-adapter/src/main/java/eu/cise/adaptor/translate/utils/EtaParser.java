package eu.cise.adaptor.translate.utils;

public class EtaParser {

    /**
     * Starting from an ETA in the format "17-09 14:30"
     * it's needed to create a string in ISO format
     * without the year like "09-07" for month and day.
     *
     * @param eta the estimates time of arrival.
     * @return the iso format string
     */
    String getMonthDayISOFormat(String eta) {

        if (monthsOrDaysAreUnavailable(eta))
            return null;

        return getMonth(eta) + "-" + getDay(eta);
    }

    private boolean monthsOrDaysAreUnavailable(String eta) {
        return Integer.valueOf(getDay(eta)).equals(0) ||
                Integer.valueOf(getMonth(eta)).equals(0);
    }

    String getMonth(String eta) {
        return getMonthDashDay(eta).split("-")[1];
    }

    String getDay(String eta) {
        return getMonthDashDay(eta).split("-")[0];
    }

    String getMonthDashDay(String eta) {
        return eta.split(" ")[0];
    }

    String getHours(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[0];
    }

    String getMinutes(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[1];
    }

    String getHoursColumnMinutes(String etaStr) {
        return etaStr.split(" ")[1];
    }

    String getDefaultHours(String eta, String defaultValue) {
        return getHours(eta).equals("24") ? defaultValue : getHours(eta);
    }

    String getDefaultMinutes(String eta, String defaultValue) {
        return getMinutes(eta).equals("60") ? defaultValue : getMinutes(eta);
    }

    String getTime(String eta) {
        return getDefaultHours(eta, "00") + ":" + getDefaultMinutes(eta, "00") + ":00.000Z";
    }

}
