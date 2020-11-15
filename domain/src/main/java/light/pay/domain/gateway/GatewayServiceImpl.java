package light.pay.domain.gateway;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.UserType;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.api.gateway.GatewayService;
import light.pay.api.gateway.request.PayRequest;
import light.pay.api.gateway.request.RegisterCustomerRequest;
import light.pay.api.gateway.request.RegisterMerchantRequest;
import light.pay.api.gateway.request.TopupRequest;
import light.pay.api.gateway.response.PayResponse;
import light.pay.api.gateway.response.RegisterCustomerResponse;
import light.pay.api.gateway.response.RegisterMerchantResponse;
import light.pay.api.gateway.response.TopupResponse;
import light.pay.api.transactions.TransactionService;
import light.pay.api.transactions.TransactionType;
import light.pay.api.transactions.request.InitiateTransactionRequest;
import light.pay.api.transactions.response.TransactionDTO;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.api.wallets.request.TopupWalletRequest;
import light.pay.api.wallets.response.WalletDTO;

import java.util.Collections;
import java.util.UUID;

public class GatewayServiceImpl implements GatewayService {

    private static final Error NOT_SUPPORTED_ERROR = Error.builder()
            .code(Errors.USER_NOT_SUPPORT_TRANSACTION_FLOW)
            .entity("user_id")
            .cause("")
            .build();

    private AccountService accountService;
    private WalletService walletService;
    private TransactionService transactionService;

    public GatewayServiceImpl(AccountService accountService, WalletService walletService, TransactionService transactionService) {
        this.accountService = accountService;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public Response<RegisterCustomerResponse> registerCustomer(RegisterCustomerRequest request) {
        return registerAccount(request.getName(), request.getEmail(), request.getPhoneNumber(), UserType.CUSTOMER)
                .map(userId -> RegisterCustomerResponse.builder()
                        .userID(userId)
                        .name(request.getName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .build());
    }

    @Override
    public Response<RegisterMerchantResponse> registerMerchant(RegisterMerchantRequest request) {
        return registerAccount(request.getName(), request.getEmail(), "", UserType.MERCHANT)
                .map(userId -> RegisterMerchantResponse.builder()
                        .merchantID(userId)
                        .name(request.getName())
                        .email(request.getEmail())
                        .build());
    }

    @Override
    public Response<TopupResponse> topup(TopupRequest request) {
        Response<WalletDTO> walletByUserId = findAccountAndWallet(request.getUserID(), UserType.CUSTOMER);
        if (!walletByUserId.isSuccess()) {
            return (Response) walletByUserId;
        }

        String walletId = walletByUserId.getData().getWalletId();
        String transactionsId = UUID.randomUUID().toString();

        InitiateTransactionRequest initiateTransactionRequest = InitiateTransactionRequest.builder()
                .transactionID(transactionsId)
                .amount(request.getAmount())
                .creditedWallet(walletId)
                .debitedWallet("") //this should be bank wallet
                .description(request.getDescription())
                .referenceID(request.getReferenceID())
                .transactionType(TransactionType.TOPUP)
                .build();

        Response<TransactionDTO> initiateTrxResponse = transactionService.initiateTransaction(initiateTransactionRequest);
        if (!initiateTrxResponse.isSuccess()) {
            return (Response) initiateTrxResponse;
        }
        TransactionDTO initiatedTrx = initiateTrxResponse.getData();

        TopupWalletRequest topupWalletRequest = TopupWalletRequest.builder()
                .amount(request.getAmount())
                .walletID(walletId)
                .build();

        return walletService.topupWallet(topupWalletRequest)
                .flatMap(v -> transactionService.completeTransaction(initiatedTrx.getTransactionID()))
                .map(completedTrx -> TopupResponse.builder()
                        .userID(request.getUserID())
                        .transactionID(completedTrx.getTransactionID())
                        .amount(completedTrx.getAmount())
                        .description(completedTrx.getDescription())
                        .referenceID(completedTrx.getReferenceID())
                        .build());
    }

    @Override
    public Response<PayResponse> pay(PayRequest request) {
        return null;
    }

    private Response<String> registerAccount(String name, String email, String phoneNumber, UserType userType) {
        String userId = UUID.randomUUID().toString();
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userID(userId)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .userType(userType)
                .build();

        Response<String> accountResponse = accountService.createAccount(createAccountRequest);
        if (!accountResponse.isSuccess()) {
            return (Response) accountResponse;
        }

        String walletId = UUID.randomUUID().toString();
        CreateWalletRequest createWalletRequest = CreateWalletRequest.builder()
                .userID(userId)
                .walletID(walletId)
                .build();

        Response<String> walletResponse = walletService.createWallet(createWalletRequest);
        if (!walletResponse.isSuccess()) {
            return (Response) walletResponse;
        }

        return Response.createSuccessResponse(userId);
    }

    private Response<WalletDTO> findAccountAndWallet(String userId, UserType expectedType) {
        return accountService
                .findAccount(userId)
                .validate(accountDto -> expectedType.equals(accountDto.getUserType()), Collections.singletonList(NOT_SUPPORTED_ERROR))
                .flatMap(accountDTO -> walletService.findWalletByUserId(userId));
    }
}
