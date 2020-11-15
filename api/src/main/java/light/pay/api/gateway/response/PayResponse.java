package light.pay.api.gateway.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PayResponse {
    @SerializedName("transaction_id")
    private String transactionID;

    @SerializedName("payerId")
    private String payerId;

    @SerializedName("payeeId")
    private String payeeId;

    private Long amount;

    @SerializedName("reference_id")
    private String referenceID;

    private String description;
}
