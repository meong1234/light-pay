package light.pay.domain.wallets.repository;

import light.pay.domain.wallets.entity.Wallet;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(WalletMapper.class)
public interface WalletDbInterface {
    @SqlUpdate("INSERT INTO wallets (wallet_id, user_id, balance) VALUES (:w.walletID, :w.userID, :w.balance)")
    @GetGeneratedKeys
    long insert(@BindBean("w") Wallet wallet);

    @SqlQuery("SELECT id, wallet_id, user_id, balance FROM wallets WHERE user_id = :user_id")
    Wallet getByUserId(@Bind("user_id") String userID);

    @SqlQuery("SELECT id, wallet_id, user_id, balance FROM wallets WHERE wallet_id = :walletId")
    Wallet getByWalletID(@Bind("wallet_id") String walletId);

    @SqlUpdate("UPDATE wallets SET balance = :newBalance where wallet_id = :wallet_id")
    int updateBalance(@Bind("wallet_id") String walletId, @Bind("newBalance") Long newBalance);
}
