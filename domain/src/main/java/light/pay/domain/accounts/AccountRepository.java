package light.pay.domain.accounts;

import light.pay.api.response.Response;
import light.pay.domain.accounts.entity.Account;

public interface AccountRepository {
    Response<Long> insert(Account user);
    Response<Account> getByUserId(String userID);
}
