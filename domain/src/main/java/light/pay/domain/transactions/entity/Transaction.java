package light.pay.domain.transactions.entity;

import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.TransactionType;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.sql.Timestamp;
import java.time.Instant;

@Value
@Builder
public class Transaction {
    @With
    private Long id;
    private String transactionID;
    private String referenceID;
    private String creditedWallet;
    private String debitedWallet;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Long amount;
    private TransactionType transactionType;
    private TransactionStatus status;

    public Transaction markAsComplete() {
        return new Transaction(id, transactionID, referenceID, creditedWallet, debitedWallet, description, createdAt,
                Timestamp.from(Instant.now()), amount, transactionType, TransactionStatus.COMPLETED);
    }
}
