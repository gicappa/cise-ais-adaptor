package eu.cise.adaptor.signature;

public class PrivateKeyInfo {

    private final String id;
    private final String password;

    public PrivateKeyInfo(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String keyAlias() {
        return id + ".key";
    }

    public String password() {
        return password;
    }
}
