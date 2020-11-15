package light.pay.domain.gateway;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.api.gateway.GatewayService;
import light.pay.api.transactions.TransactionService;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class GatewayServiceImplTest {
    private static final Response GENERIC_ERROR = Response.createErrorResponse(Errors.GENERIC_ERROR_CODE, "something", "");

    private GatewayService gatewayService;
    private AccountService mockAccountService;
    private WalletService mockWalletService;
    private TransactionService mockTransactionService;

    @BeforeEach
    void setUp() {
        mockAccountService = mock(AccountService.class);
        mockWalletService = mock(WalletService.class);
        Mockito.when(mockAccountService.createAccount(any(CreateAccountRequest.class))).thenReturn(GENERIC_ERROR);
        Mockito.when(mockWalletService.createWallet(any(CreateWalletRequest.class))).thenReturn(GENERIC_ERROR);

        gatewayService = new GatewayServiceImpl(mockAccountService, mockWalletService, mockTransactionService);
    }

    @Nested
    @DisplayName("GatewayService.RegisterCustomer")
    class RegisterCustomerTest {

        @Test
        @DisplayName("should return createAccount errors, if accountService return errors")
        void shouldReturnCreateAccountErrors() {
        }

        @Test
        @DisplayName("should return createWallets errors, if walletservice return errors")
        void shouldReturnCreateWalletsErrors() {
        }

        @Test
        @DisplayName("should return create new Customer Account and Wallet")
        void shouldCreateCustomerAccountAndCreateNewWallet() {

        }
    }

}