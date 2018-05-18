package eu.cise.adaptor.signature;

public class KeyStoreInfo {

    private final String name;
    private final String password;

    public KeyStoreInfo(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }
}
