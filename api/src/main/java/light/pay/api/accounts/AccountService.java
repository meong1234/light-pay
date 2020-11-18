package light.pay.api.accounts;

import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.accounts.response.AccountDTO;
import light.pay.api.response.Response;

public interface AccountService {
    Response<String> createAccount(CreateAccountRequest request);
    Response<AccountDTO> findAccount(String userId);
}
