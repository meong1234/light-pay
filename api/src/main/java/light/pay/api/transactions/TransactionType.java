package light.pay.api.transactions;

public enum TransactionType {
    TOPUP("TOPUP"),
    PAYMENT("PAYMENT");

    private final String type;

    TransactionType(final String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
