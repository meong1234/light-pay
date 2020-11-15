package light.pay.api.wallets.request;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TopupWalletRequest {
    @SerializedName("wallet_id")
    private String walletID;

    private Long amount;
}
