package light.pay.api.gateway.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterCustomerResponse {
    @SerializedName("user_id")
    private String userID;

    private String name;

    private String email;

    private String phoneNumber;
}
