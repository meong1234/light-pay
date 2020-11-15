package light.pay.api.wallets.response;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
public class WalletDTO {
    @SerializedName("wallet_id")
    private String walletId;
    private Long balance;
}
