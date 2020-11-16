package light.pay.domain.accounts.repository;

import light.pay.api.accounts.UserType;
import light.pay.domain.accounts.entity.Account;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper implements ResultSetMapper<Account> {
    @Override
    public Account map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Account user = Account.builder()
                .id(r.getLong("id"))
                .userID(r.getString("user_id"))
                .name(r.getString("name"))
                .email(r.getString("email"))
                .phoneNumber(r.getString("phone_number"))
                .userType(UserType.valueOf(r.getString("user_type")))
                .build();

        return user;
    }
}
