package light.pay.domain.constants;

public class DomainConstants {
    public static class Customer {
        public static int CUSTOMER_TYPE = 0;
        public static int MERCHANT_TYPE = 1;
    }

    public static class TransactionType {
        public static int TOPUP_TYPE = 0;
        public static int PAYMENT_TYPE = 1;
    }

    public static class TransactionStatus {
        public static int INITIATED = 0;
        public static int COMPLETED = 1;
    }
}
