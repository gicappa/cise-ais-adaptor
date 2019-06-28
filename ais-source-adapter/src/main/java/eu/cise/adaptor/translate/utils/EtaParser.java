package eu.cise.adaptor.translate.utils;

public class EtaParser {
    String getDefaultMonth(String eta, String defaultValue) {
        return getMonth(eta).equals("00") ? defaultValue : getMonth(eta);
    }

    String getDefaultDay(String eta, String defaultValue) {
        return getMonth(eta).equals("00") ? defaultValue : getDay(eta);
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

    String getHoursColumnMinutes(String etaStr) {
        return etaStr.split(" ")[1];
    }

    String getHours(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[0];
    }

    String getMinutes(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[1];
    }

     String getDate(int year, String eta) {
        return year + "-" +
                getDefaultMonth(eta, "01") + "-" +
                getDefaultDay(eta, "01");
    }

    String getDay(String eta) {
        return getMonthDashDay(eta).split("-")[0];
    }

    String getMonth(String eta) {
        return getMonthDashDay(eta).split("-")[1];
    }

    String getMonthDashDay(String eta) {
        return eta.split(" ")[0];
    }

}
