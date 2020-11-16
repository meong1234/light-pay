package light.pay.domain.wallets.repository;

import light.pay.domain.wallets.entity.Wallet;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletMapper implements ResultSetMapper<Wallet> {
    @Override
    public Wallet map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Wallet wallet = Wallet.builder()
                .id(r.getLong("id"))
                .userID(r.getString("user_id"))
                .walletID(r.getString("wallet_id"))
                .balance(r.getLong("balance"))
                .build();

        return wallet;
    }
}
