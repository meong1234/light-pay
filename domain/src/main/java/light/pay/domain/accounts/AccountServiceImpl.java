package light.pay.domain.accounts;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.accounts.response.AccountDTO;
import light.pay.api.errors.Response;
import light.pay.domain.accounts.entity.Account;

public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Response<String> createAccount(CreateAccountRequest request) {
        //you can add validation here

        Account account = Account.builder()
                .userID(request.getUserID())
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .build();

        return accountRepository.insert(account)
                .map(id -> request.getUserID());
    }

    @Override
    public Response<AccountDTO> findAccount(String userId) {
        return accountRepository.getByUserId(userId)
                .map(account -> AccountDTO.builder()
                        .userID(account.getUserID())
                        .name(account.getName())
                        .email(account.getEmail())
                        .phoneNumber(account.getPhoneNumber())
                        .userType(account.getUserType())
                        .build());
    }
}
