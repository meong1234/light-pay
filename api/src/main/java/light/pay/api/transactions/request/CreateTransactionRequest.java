package light.pay.api.transactions.request;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateTransactionRequest {
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

    @SerializedName("transaction_type")
    private int transactionType;
}
