package eu.cise.adaptor;

public enum DelimiterType {
    STRIP("strip"), KEEP("value");

    private final String value;

    DelimiterType(String value) {
        this.value = value;
    }
}
