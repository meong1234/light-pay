package light.pay.domain.wallets.repository;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.response.Response;
import light.pay.commons.db.Repository;
import light.pay.domain.wallets.WalletRepository;
import light.pay.domain.wallets.entity.Wallet;

import java.util.Collections;

public class WalletRepositoryImpl extends Repository<WalletDbInterface> implements WalletRepository {
    @Override
    public Response<Long> insert(Wallet wallet) {
        return execute(WalletDbInterface.class, repo -> repo.insert(wallet));
    }

    @Override
    public Response<Wallet> getByUserId(String userID) {
        return execute(WalletDbInterface.class, repo -> repo.getByUserId(userID))
                .validate(this::isExists, Collections.singletonList(new Error( "user_id",  Errors.WALLET_NOT_FOUND_ERROR_CODE,"")));
    }

    @Override
    public Response<Wallet> getByWalletId(String walletId) {
        return execute(WalletDbInterface.class, repo -> repo.getByWalletID(walletId))
                .validate(this::isExists, Collections.singletonList(new Error( "wallet_id",  Errors.WALLET_NOT_FOUND_ERROR_CODE,"")));
    }

    @Override
    public Response<Void> updateBalance(Wallet wallet) {
        return execute(WalletDbInterface.class, repo -> repo.updateBalance(wallet.getWalletID(), wallet.getBalance()))
                .map(i -> null);
    }
}
