package light.pay.api.wallets.request;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateWalletRequest {
    @SerializedName("user_id")
    private String userID;

    @SerializedName("wallet_id")
    private String walletID;
}
