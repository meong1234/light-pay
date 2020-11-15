package light.pay.domain.gateway;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.UserType;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.accounts.response.AccountDTO;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.api.gateway.GatewayService;
import light.pay.api.gateway.request.RegisterCustomerRequest;
import light.pay.api.gateway.request.RegisterMerchantRequest;
import light.pay.api.gateway.request.TopupRequest;
import light.pay.api.gateway.response.RegisterCustomerResponse;
import light.pay.api.gateway.response.RegisterMerchantResponse;
import light.pay.api.gateway.response.TopupResponse;
import light.pay.api.transactions.TransactionService;
import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.TransactionType;
import light.pay.api.transactions.request.InitiateTransactionRequest;
import light.pay.api.transactions.response.TransactionDTO;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.api.wallets.request.TopupWalletRequest;
import light.pay.api.wallets.response.WalletDTO;
import light.pay.commons.marshalling.JsonUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class GatewayServiceImplTest {
    private static final Response GENERIC_ERROR = Response
            .createErrorResponse(Errors.GENERIC_ERROR_CODE, "something", "");
    private static final Response USER_NOT_FOUND_ERROR = Response
            .createErrorResponse(Errors.USER_NOT_FOUND_ERROR_CODE, "user_id", "");
    private static final Response WALLET_NOT_FOUND_ERROR = Response
            .createErrorResponse(Errors.WALLET_NOT_FOUND_ERROR_CODE, "wallet_id", "");
    private static final Response TRANSACTION_ALREADY_EXISTS_ERROR = Response
            .createErrorResponse(Errors.TRANSACTION_ALREADY_EXISTS_ERROR_CODE, "reference_id", "");

    private GatewayService gatewayService;
    private AccountService mockAccountService;
    private WalletService mockWalletService;
    private TransactionService mockTransactionService;
    private EasyRandom objectGenerator = new EasyRandom();


    @BeforeEach
    void setUp() {
        mockAccountService = mock(AccountService.class);
        mockWalletService = mock(WalletService.class);
        mockTransactionService = mock(TransactionService.class);
        when(mockAccountService.createAccount(any(CreateAccountRequest.class))).thenReturn(GENERIC_ERROR);
        when(mockWalletService.createWallet(any(CreateWalletRequest.class))).thenReturn(GENERIC_ERROR);

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
            when(mockAccountService.createAccount(any(CreateAccountRequest.class)))
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

            when(mockAccountService.createAccount(argThat(isValidCreateAccountRequest(request))))
                    .thenReturn(Response.createSuccessResponse("successUserId"));

            when(mockWalletService.createWallet(any(CreateWalletRequest.class)))
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
                    && createAccountRequest.getUserType() == UserType.CUSTOMER;
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
            when(mockAccountService.createAccount(any(CreateAccountRequest.class)))
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

            when(mockAccountService.createAccount(argThat(isValidCreateAccountRequest(request))))
                    .thenReturn(Response.createSuccessResponse("successUserId"));

            when(mockWalletService.createWallet(any(CreateWalletRequest.class)))
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
                    && createAccountRequest.getUserType() == UserType.MERCHANT;
        }
    }


    @Nested
    @DisplayName("GatewayService.CustomerTopup")
    class CustomerTopup {

        @BeforeEach
        void setUp() {
            when(mockAccountService.findAccount(any(String.class))).thenReturn(USER_NOT_FOUND_ERROR);
            when(mockWalletService.findWalletByUserId(any(String.class))).thenReturn(WALLET_NOT_FOUND_ERROR);
            when(mockTransactionService.initiateTransaction(any(InitiateTransactionRequest.class))).thenReturn(TRANSACTION_ALREADY_EXISTS_ERROR);
        }

        @Test
        @DisplayName("should return user-not-found errors, if user-id not found in account-service")
        void shouldReturnUserNotFoundErrors() {
            TopupRequest request = objectGenerator.nextObject(TopupRequest.class);
            Response<TopupResponse> response = gatewayService.topup(request);

            assertEquals(USER_NOT_FOUND_ERROR, response);

            verify(mockAccountService, times(1)).findAccount(anyString());
            verify(mockWalletService, times(0)).findWalletByUserId(anyString());
            verify(mockTransactionService, times(0)).initiateTransaction(any(InitiateTransactionRequest.class));
        }

        @Test
        @DisplayName("should return wallet-not-found errors, if wallet-id not found in wallet-service")
        void shouldReturnWalletNotFoundErrors() {
            TopupRequest request = objectGenerator.nextObject(TopupRequest.class);

            AccountDTO accountDTO = objectGenerator
                    .nextObject(AccountDTO.class)
                    .withUserID(request.getUserID());
            when(mockAccountService.findAccount(eq(request.getUserID()))).thenReturn(Response.createSuccessResponse(accountDTO));

            Response<TopupResponse> response = gatewayService.topup(request);

            assertEquals(WALLET_NOT_FOUND_ERROR, response);

            verify(mockAccountService, times(1)).findAccount(eq(request.getUserID()));
            verify(mockWalletService, times(1)).findWalletByUserId(eq(request.getUserID()));
            verify(mockTransactionService, times(0)).initiateTransaction(any(InitiateTransactionRequest.class));
        }

        @Test
        @DisplayName("should return transaction-duplicate errors, if reference-id already exists in transaction-service")
        void shouldReturnTransactionDuplicateErrors() {
            TopupRequest request = objectGenerator.nextObject(TopupRequest.class);

            AccountDTO accountDTO = objectGenerator
                    .nextObject(AccountDTO.class)
                    .withUserID(request.getUserID());
            when(mockAccountService.findAccount(eq(request.getUserID()))).thenReturn(Response.createSuccessResponse(accountDTO));

            WalletDTO walletDTO = objectGenerator.nextObject(WalletDTO.class);

            when(mockWalletService.findWalletByUserId(eq(request.getUserID()))).thenReturn(Response.createSuccessResponse(walletDTO));

            Response<TopupResponse> response = gatewayService.topup(request);

            assertEquals(TRANSACTION_ALREADY_EXISTS_ERROR, response);

            verify(mockAccountService, times(1)).findAccount(eq(request.getUserID()));
            verify(mockWalletService, times(1)).findWalletByUserId(eq(request.getUserID()));
            verify(mockTransactionService, times(1)).initiateTransaction(any(InitiateTransactionRequest.class));
        }

        @Test
        @DisplayName("should create new transactions and add customer balance")
        void shouldCreateNewTransactionAndAddCustomerBalance() {
            TopupRequest request = objectGenerator.nextObject(TopupRequest.class);

            AccountDTO accountDTO = objectGenerator
                    .nextObject(AccountDTO.class)
                    .withUserID(request.getUserID());
            when(mockAccountService.findAccount(eq(request.getUserID()))).thenReturn(Response.createSuccessResponse(accountDTO));

            WalletDTO walletDTO = objectGenerator.nextObject(WalletDTO.class);

            when(mockWalletService.findWalletByUserId(eq(request.getUserID()))).thenReturn(Response.createSuccessResponse(walletDTO));

            when(mockTransactionService.initiateTransaction(any(InitiateTransactionRequest.class)))
                    .thenAnswer(initiateTransactionAnswer(request, walletDTO));

            TopupWalletRequest topupWalletRequest = TopupWalletRequest.builder()
                    .walletID(walletDTO.getWalletId())
                    .amount(request.getAmount())
                    .build();

            when(mockWalletService.topupWallet(eq(topupWalletRequest)))
                    .thenReturn(Response.createSuccessResponse(null));

            when(mockTransactionService.completeTransaction(any(String.class)))
                    .thenAnswer(completedTransactionAnswer(request, walletDTO));

            Response<TopupResponse> response = gatewayService.topup(request);
            assertTrue(response.isSuccess());

            ArgumentCaptor<InitiateTransactionRequest> createTransactionRequestArgumentCaptor = ArgumentCaptor.forClass(InitiateTransactionRequest.class);
            verify(mockTransactionService, times(1)).initiateTransaction(createTransactionRequestArgumentCaptor.capture());
            InitiateTransactionRequest value = createTransactionRequestArgumentCaptor.getValue();

            TopupResponse expectedResponse = TopupResponse.builder()
                    .userID(request.getUserID())
                    .transactionID(value.getTransactionID())
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .referenceID(request.getReferenceID())
                    .build();

            assertEquals(expectedResponse, response.getData());

            verify(mockAccountService, times(1)).findAccount(eq(request.getUserID()));
            verify(mockWalletService, times(1)).findWalletByUserId(eq(request.getUserID()));
            verify(mockWalletService, times(1)).topupWallet(any(TopupWalletRequest.class));
            verify(mockTransactionService, times(1)).initiateTransaction(any(InitiateTransactionRequest.class));
            verify(mockTransactionService, times(1)).completeTransaction(anyString());

        }

        private Answer<Object> initiateTransactionAnswer(TopupRequest request, WalletDTO walletDTO) {
            return (InvocationOnMock invocation) -> {
                Object arg = invocation.getArguments()[0];
                InitiateTransactionRequest argRequest = (InitiateTransactionRequest) arg;

                if (!isValidTopupRequest(argRequest, request, walletDTO.getWalletId())) {
                    return GENERIC_ERROR;
                }

                TransactionDTO transactionDTO = getTransactionDTO(argRequest.getTransactionID(), request, walletDTO,
                        TransactionType.TOPUP, TransactionStatus.INITIATED);

                return Response.createSuccessResponse(transactionDTO);
            };
        }

        private Answer<Object> completedTransactionAnswer(TopupRequest request, WalletDTO walletDTO) {
            return (InvocationOnMock invocation) -> {
                String transactionId = (String) invocation.getArguments()[0];

                TransactionDTO transactionDTO = getTransactionDTO(transactionId, request, walletDTO,
                        TransactionType.TOPUP, TransactionStatus.COMPLETED);

                return Response.createSuccessResponse(transactionDTO);
            };
        }

        private TransactionDTO getTransactionDTO(String transactionId, TopupRequest request, WalletDTO walletDTO,
                                                 TransactionType transactionType, TransactionStatus transactionStatus) {
            return TransactionDTO.builder()
                    .transactionID(transactionId)
                    .amount(request.getAmount())
                    .creditedWallet(walletDTO.getWalletId())
                    .debitedWallet("")
                    .description(request.getDescription())
                    .referenceID(request.getReferenceID())
                    .transactionType(transactionType)
                    .createdAt(OffsetDateTime.now())
                    .status(transactionStatus)
                    .build();
        }

        private boolean isValidTopupRequest(InitiateTransactionRequest argRequest, TopupRequest request, String walletId) {
            return argRequest.getAmount() == request.getAmount()
                    && argRequest.getCreditedWallet().equals(walletId)
                    && argRequest.getDebitedWallet().equals("")
                    && argRequest.getDescription().equals(request.getDescription())
                    && argRequest.getReferenceID().equals(request.getReferenceID())
                    && argRequest.getTransactionType() == TransactionType.TOPUP;
        }
    }

}