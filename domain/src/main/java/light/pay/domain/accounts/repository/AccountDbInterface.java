package light.pay.domain.accounts.repository;

import light.pay.domain.accounts.entity.Account;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(AccountMapper.class)
public interface AccountDbInterface {
    @SqlUpdate("INSERT INTO accounts (user_id, name, email, phone_number, user_type) VALUES (:w.userID, :w.name, :w.email, :w.phoneNumber, :w.userType)")
    @GetGeneratedKeys
    long insert(@BindBean("w") Account userAccount);

    @SqlQuery("SELECT id, user_id, name, email, phone_number, user_type FROM accounts WHERE user_id = :user_id")
    Account getByUserId(@Bind("user_id") String userID);
}
