package light.pay.api.accounts.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountDTO {
    @SerializedName("user_id")
    private String userID;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean active;
    @SerializedName("user_type")
    private int userType;
}
