package light.pay.domain.accounts.repository;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.commons.db.Repository;
import light.pay.domain.accounts.AccountRepository;
import light.pay.domain.accounts.entity.Account;

import java.util.Collections;

public class AccountRepositoryImpl extends Repository<AccountDbInterface> implements AccountRepository {
    @Override
    public Response<Long> insert(Account user) {
        return execute(AccountDbInterface.class, repo -> repo.insert(user));
    }

    @Override
    public Response<Account> getByUserId(String userID) {
        return execute(AccountDbInterface.class, repo -> repo.getByUserId(userID))
                .validate(this::isExists, Collections.singletonList(new Error( "user_id",  Errors.USER_NOT_FOUND_ERROR_CODE,"")));
    }
}
