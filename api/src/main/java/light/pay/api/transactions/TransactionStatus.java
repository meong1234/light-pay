package light.pay.api.transactions;

public enum TransactionStatus {
    INITIATED("INITIATED"),
    COMPLETED("COMPLETED");

    private final String type;

    TransactionStatus(final String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
