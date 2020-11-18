package light.pay.domain.accounts;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.accounts.response.AccountDTO;
import light.pay.api.response.Response;
import light.pay.domain.accounts.entity.Account;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {
    private AccountService accountService;
    private AccountRepository mockRepository;
    private EasyRandom objectGenerator = new EasyRandom();

    @BeforeEach
    void setUp() {
        mockRepository = mock(AccountRepository.class);
        accountService = new AccountServiceImpl(mockRepository);
    }

    @Test
    void shouldCallRepositoryInsertOnCrateNewAccount() {
        CreateAccountRequest request = objectGenerator.nextObject(CreateAccountRequest.class);

        when(mockRepository.insert(argThat(isValidAccount(request)))).thenReturn(Response.createSuccessResponse(1L));

        Response<String> response = accountService.createAccount(request);
        assertTrue(response.isSuccess());
        assertEquals(request.getUserID(), response.getData());

        verify(mockRepository, times(1)).insert(any(Account.class));
    }

    @Test
    void shouldCallRepositoryFindByUserIdOnFindAccount() {
        String request = objectGenerator.nextObject(String.class);
        Account data = objectGenerator.nextObject(Account.class);

        when(mockRepository.getByUserId(eq(request))).thenReturn(Response.createSuccessResponse(data));

        Response<AccountDTO> response = accountService.findAccount(request);
        assertTrue(response.isSuccess());
        AccountDTO actual = response.getData();
        assertEquals(data.getName(), actual.getName());
        assertEquals(data.getEmail(), actual.getEmail());
        assertEquals(data.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(data.getUserID(), actual.getUserID());
        assertEquals(data.getUserType(), actual.getUserType());

        verify(mockRepository, times(1)).getByUserId(anyString());
    }

    private ArgumentMatcher<Account> isValidAccount(CreateAccountRequest request) {
        return account -> account.getName().equals(request.getName())
                    && account.getEmail().equals(request.getEmail())
                    && account.getPhoneNumber().equals(request.getPhoneNumber())
                    && account.getUserID().equals(request.getUserID())
                    && account.getUserType().equals(request.getUserType());
        }
}