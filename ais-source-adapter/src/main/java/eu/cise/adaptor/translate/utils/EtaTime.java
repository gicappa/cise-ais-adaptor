package eu.cise.adaptor.translate.utils;

public class EtaTime {

    String getTime(String eta) {
        return getDefaultHours(eta, "00") + ":" + getDefaultMinutes(eta, "00") + ":00.000Z";
    }

    private String getHours(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[0];
    }

    private String getMinutes(String etaStr) {
        return getHoursColumnMinutes(etaStr).split(":")[1];
    }

    private String getHoursColumnMinutes(String etaStr) {
        return etaStr.split(" ")[1];
    }

    private String getDefaultHours(String eta, String defaultValue) {
        return getHours(eta).equals("24") ? defaultValue : getHours(eta);
    }

    private String getDefaultMinutes(String eta, String defaultValue) {
        return getMinutes(eta).equals("60") ? defaultValue : getMinutes(eta);
    }

}
