package light.pay.domain.accounts.repository;

import light.pay.api.accounts.UserType;
import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.commons.db.DatabaseUtils;
import light.pay.domain.accounts.AccountRepository;
import light.pay.domain.accounts.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryImplTest {

    private AccountRepository accountRepository = new AccountRepositoryImpl();

    @BeforeEach
    void setUp() {
        DatabaseUtils.truncate("accounts");
    }

    @Test
    void insertShouldExistsInDB() {
        String userID = UUID.randomUUID().toString();
        Account account = Account.builder()
                .name("sebuahnama")
                .email("sebuahemail@gmail.com")
                .phoneNumber("081108110811")
                .userID(userID)
                .userType(UserType.CUSTOMER)
                .build();

        accountRepository.insert(account);

        Response<Account> response = accountRepository.getByUserId(userID);
        assertTrue(response.isSuccess());

        Account actual = response.getData();

        assertEquals(account.getName(), actual.getName());
        assertEquals(account.getUserID(), actual.getUserID());
        assertEquals(account.getEmail(), actual.getEmail());
        assertEquals(account.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(account.getUserType(), actual.getUserType());
    }

    @Test
    void shouldReturnUserNotFoundErrorIfNotExistsInDB() {
        Response<Account> response = accountRepository.getByUserId("sebuahid");
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.USER_NOT_FOUND_ERROR_CODE, errors.get(0).getCode());
        assertEquals("user_id", errors.get(0).getEntity());
    }
}