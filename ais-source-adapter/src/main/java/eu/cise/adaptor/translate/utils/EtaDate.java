package eu.cise.adaptor.translate.utils;

public class EtaDate {

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

    private String getMonth(String eta) {
        return getMonthDashDay(eta).split("-")[1];
    }

    private String getDay(String eta) {
        return getMonthDashDay(eta).split("-")[0];
    }

    private String getMonthDashDay(String eta) {
        return eta.split(" ")[0];
    }

}
