package light.pay.api.transactions.response;

import com.google.gson.annotations.SerializedName;
import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class TransactionDTO {
    @SerializedName("transaction_id")
    private String transactionID;

    @SerializedName("reference_id")
    private String referenceID;

    @SerializedName("credited_wallet")
    private String creditedWallet;

    @SerializedName("debited_wallet")
    private String debitedWallet;

    private String description;

    private Long amount;

    @SerializedName("created_at")
    private OffsetDateTime createdAt;

    @SerializedName("transaction_type")
    private TransactionType transactionType;

    private TransactionStatus status;
}
