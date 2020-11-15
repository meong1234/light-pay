package light.pay.api.gateway.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TopupResponse {
    @SerializedName("transaction_id")
    private String transactionID;

    @SerializedName("user_id")
    private String userID;

    private Long amount;

    @SerializedName("reference_id")
    private String referenceID;

    private String description;

}
