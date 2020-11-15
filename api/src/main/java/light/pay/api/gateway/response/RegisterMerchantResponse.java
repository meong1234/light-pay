package light.pay.api.gateway.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterMerchantResponse {
    @SerializedName("merchant_id")
    private String merchantID;

    private String name;

    private String email;
}
