package light.pay.domain.wallets;

import light.pay.api.response.Response;
import light.pay.domain.wallets.entity.Wallet;

public interface WalletRepository {
    Response<Long> insert(Wallet wallet);
    Response<Wallet> getByUserId(String userID);
    Response<Wallet> getByWalletId(String walletId);
    Response<Void> updateBalance(Wallet wallet);
}
