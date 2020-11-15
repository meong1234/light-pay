package light.pay.domain.gateway;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.api.gateway.GatewayService;
import light.pay.api.gateway.request.RegisterCustomerRequest;
import light.pay.api.gateway.request.RegisterMerchantRequest;
import light.pay.api.gateway.response.RegisterCustomerResponse;
import light.pay.api.gateway.response.RegisterMerchantResponse;
import light.pay.api.transactions.TransactionService;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.domain.constants.DomainConstants;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GatewayServiceImplTest {
    private static final Response GENERIC_ERROR = Response.createErrorResponse(Errors.GENERIC_ERROR_CODE, "something", "");

    private GatewayService gatewayService;
    private AccountService mockAccountService;
    private WalletService mockWalletService;
    private TransactionService mockTransactionService;
    private EasyRandom objectGenerator = new EasyRandom();


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
            RegisterCustomerRequest request = objectGenerator.nextObject(RegisterCustomerRequest.class);
            Response<RegisterCustomerResponse> response = gatewayService.registerCustomer(request);

            assertEquals(GENERIC_ERROR, response);

            verify(mockAccountService, times(1)).createAccount(any(CreateAccountRequest.class));
            verify(mockWalletService, times(0)).createWallet(any(CreateWalletRequest.class));
        }

        @Test
        @DisplayName("should return createWallets errors, if walletservice return errors")
        void shouldReturnCreateWalletsErrors() {
            Mockito.when(mockAccountService.createAccount(any(CreateAccountRequest.class)))
                    .thenReturn(Response.createSuccessResponse("successUserId"));

            RegisterCustomerRequest request = objectGenerator.nextObject(RegisterCustomerRequest.class);
            Response<RegisterCustomerResponse> response = gatewayService.registerCustomer(request);
            assertEquals(GENERIC_ERROR, response);

            verify(mockAccountService, times(1)).createAccount(any(CreateAccountRequest.class));
            verify(mockWalletService, times(1)).createWallet(any(CreateWalletRequest.class));
        }

        @Test
        @DisplayName("should return create new Customer Account and Wallet")
        void shouldCreateCustomerAccountAndCreateNewWallet() {
            RegisterCustomerRequest request = objectGenerator.nextObject(RegisterCustomerRequest.class);

            Mockito.when(mockAccountService.createAccount(argThat(isValidCreateAccountRequest(request))))
                    .thenReturn(Response.createSuccessResponse("successUserId"));

            Mockito.when(mockWalletService.createWallet(any(CreateWalletRequest.class)))
                    .thenReturn(Response.createSuccessResponse("successWalletId"));

            Response<RegisterCustomerResponse> response = gatewayService.registerCustomer(request);
            assertTrue(response.isSuccess());

            verify(mockAccountService, times(1)).createAccount(any(CreateAccountRequest.class));

            ArgumentCaptor<CreateWalletRequest> createWalletRequestArgumentCaptor = ArgumentCaptor.forClass(CreateWalletRequest.class);
            verify(mockWalletService, times(1)).createWallet(createWalletRequestArgumentCaptor.capture());
            CreateWalletRequest value = createWalletRequestArgumentCaptor.getValue();

            RegisterCustomerResponse expectedResponse = RegisterCustomerResponse.builder()
                    .userID(value.getUserID())
                    .name(request.getName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .build();
            assertEquals(expectedResponse, response.getData());
        }

        private ArgumentMatcher<CreateAccountRequest> isValidCreateAccountRequest(RegisterCustomerRequest request) {
            return createAccountRequest -> createAccountRequest.getEmail().equals(request.getEmail())
                    && createAccountRequest.getName().equals(request.getName())
                    && createAccountRequest.getPhoneNumber().equals(request.getPhoneNumber())
                    && createAccountRequest.getUserType() == DomainConstants.Customer.CUSTOMER_TYPE;
        }
    }

    @Nested
    @DisplayName("GatewayService.RegisterMerchant")
    class RegisterMerchantTest {

        @Test
        @DisplayName("should return createAccount errors, if accountService return errors")
        void shouldReturnCreateAccountErrors() {
            RegisterMerchantRequest request = objectGenerator.nextObject(RegisterMerchantRequest.class);
            Response<RegisterMerchantResponse> response = gatewayService.registerMerchant(request);

            assertEquals(GENERIC_ERROR, response);

            verify(mockAccountService, times(1)).createAccount(any(CreateAccountRequest.class));
            verify(mockWalletService, times(0)).createWallet(any(CreateWalletRequest.class));
        }

        @Test
        @DisplayName("should return createWallets errors, if walletservice return errors")
        void shouldReturnCreateWalletsErrors() {
            Mockito.when(mockAccountService.createAccount(any(CreateAccountRequest.class)))
                    .thenReturn(Response.createSuccessResponse("successUserId"));

            RegisterMerchantRequest request = objectGenerator.nextObject(RegisterMerchantRequest.class);
            Response<RegisterMerchantResponse> response = gatewayService.registerMerchant(request);
            assertEquals(GENERIC_ERROR, response);

            verify(mockAccountService, times(1)).createAccount(any(CreateAccountRequest.class));
            verify(mockWalletService, times(1)).createWallet(any(CreateWalletRequest.class));
        }

        @Test
        @DisplayName("should return create new Merchant Account and Wallet")
        void shouldCreateCustomerAccountAndCreateNewWallet() {
            RegisterMerchantRequest request = objectGenerator.nextObject(RegisterMerchantRequest.class);

            Mockito.when(mockAccountService.createAccount(argThat(isValidCreateAccountRequest(request))))
                    .thenReturn(Response.createSuccessResponse("successUserId"));

            Mockito.when(mockWalletService.createWallet(any(CreateWalletRequest.class)))
                    .thenReturn(Response.createSuccessResponse("successWalletId"));

            Response<RegisterMerchantResponse> response = gatewayService.registerMerchant(request);
            assertTrue(response.isSuccess());

            verify(mockAccountService, times(1)).createAccount(any(CreateAccountRequest.class));

            ArgumentCaptor<CreateWalletRequest> createWalletRequestArgumentCaptor = ArgumentCaptor.forClass(CreateWalletRequest.class);
            verify(mockWalletService, times(1)).createWallet(createWalletRequestArgumentCaptor.capture());
            CreateWalletRequest value = createWalletRequestArgumentCaptor.getValue();

            RegisterMerchantResponse expectedResponse = RegisterMerchantResponse.builder()
                    .merchantID(value.getUserID())
                    .name(request.getName())
                    .email(request.getEmail())
                    .build();
            assertEquals(expectedResponse, response.getData());
        }

        private ArgumentMatcher<CreateAccountRequest> isValidCreateAccountRequest(RegisterMerchantRequest request) {
            return createAccountRequest -> createAccountRequest.getEmail().equals(request.getEmail())
                    && createAccountRequest.getName().equals(request.getName())
                    && createAccountRequest.getUserType() == DomainConstants.Customer.MERCHANT_TYPE;
        }
    }



}