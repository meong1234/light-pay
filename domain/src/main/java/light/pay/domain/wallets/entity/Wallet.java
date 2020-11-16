package light.pay.domain.wallets.entity;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class Wallet {
    private Long id;
    private String walletID;
    private String userID;
    private Long balance;

    public Wallet addBalance(Long balance) {
        Long newBalance = getBalance() + balance;
        return new Wallet(id, walletID, userID, newBalance);
    }

    public Wallet subtractBalance(Long balance) {
        Long newBalance = getBalance() - balance;
        return new Wallet(id, walletID, userID, newBalance);
    }
}
