package light.pay.api.gateway.request;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PayRequest {
    @SerializedName("payer_id")
    private String payerId;

    @SerializedName("payee_id")
    private String payeeId;

    private Long amount;

    @SerializedName("reference_id")
    private String referenceID;

    private String description;
}
