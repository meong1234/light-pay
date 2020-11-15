package light.pay.api.accounts;

public enum UserType {
    CUSTOMER("CUSTOMER"),
    MERCHANT("MERCHANT");

    private final String type;

    UserType(final String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
