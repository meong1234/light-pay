package light.pay.server.integration;

import com.google.gson.reflect.TypeToken;
import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.UserType;
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
import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.TransactionType;
import light.pay.api.wallets.WalletService;
import light.pay.commons.marshalling.JsonUtils;
import light.pay.domain.accounts.AccountServiceImpl;
import light.pay.domain.accounts.entity.Account;
import light.pay.domain.accounts.repository.AccountRepositoryImpl;
import light.pay.domain.gateway.GatewayServiceImpl;
import light.pay.domain.transactions.TransactionServiceImpl;
import light.pay.domain.transactions.entity.Transaction;
import light.pay.domain.transactions.repository.TransactionRepositoryImpl;
import light.pay.domain.wallets.WalletServiceImpl;
import light.pay.domain.wallets.entity.Wallet;
import light.pay.domain.wallets.repository.WalletRepositoryImpl;
import light.pay.server.Router;
import lombok.Value;
import okhttp3.*;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllIntegrationTest {

    private static Router router;
    private static WalletRepositoryImpl walletRepository;
    private static AccountRepositoryImpl accountRepository;
    private static TransactionRepositoryImpl transactionRepository;
    private EasyRandom objectGenerator = new EasyRandom();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private static String BASE_URL = "http://127.0.0.1:8081";

    @BeforeAll
    static void beforeAll() {
        DatabaseUtils.truncate("accounts");
        DatabaseUtils.truncate("wallets");
        DatabaseUtils.truncate("transactions");

        walletRepository = new WalletRepositoryImpl();

        accountRepository = new AccountRepositoryImpl();

        transactionRepository = new TransactionRepositoryImpl();

        WalletService walletService = new WalletServiceImpl(walletRepository);

        AccountService accountService = new AccountServiceImpl(accountRepository);

        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);

        GatewayService service = new GatewayServiceImpl(accountService, walletService, transactionService);
        router =  new Router(service);
        router.configure();
    }

    @AfterAll
    static void afterAll() {
        router.stopServer();
    }

    @Test
    void shouldRegisterCustomer() throws IOException {
        RegisterCustomerRequest request = objectGenerator.nextObject(RegisterCustomerRequest.class);

        Type responseType = new TypeToken<light.pay.api.response.Response<RegisterCustomerResponse>>() {}.getType();

        RegisterCustomerResponse data = verifyAPI("/v1/customer", request, responseType);

        RegisterCustomerResponse expectedResponse = RegisterCustomerResponse.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        assertThat(data).isEqualToIgnoringGivenFields(expectedResponse, "userID");

        light.pay.api.response.Response<Wallet> walletResponse = walletRepository.getByUserId(data.getUserID());

        assertTrue(walletResponse.isSuccess());
        assertEquals(data.getUserID(), walletResponse.getData().getUserID());
        assertEquals(0, walletResponse.getData().getBalance());

        light.pay.api.response.Response<Account> accountResponse = accountRepository.getByUserId(data.getUserID());
        assertTrue(accountResponse.isSuccess());

        Account accountResponseData = accountResponse.getData();

        assertEquals(data.getUserID(), accountResponseData.getUserID());
        assertEquals(request.getName(), accountResponseData.getName());
        assertEquals(request.getEmail(), accountResponseData.getEmail());
        assertEquals(request.getPhoneNumber(), accountResponseData.getPhoneNumber());
        assertEquals(UserType.CUSTOMER, accountResponseData.getUserType());
    }

    @Test
    void shouldRegisterMerchant() throws IOException {
        RegisterMerchantRequest request = objectGenerator.nextObject(RegisterMerchantRequest.class);

        Type responseType = new TypeToken<light.pay.api.response.Response<RegisterMerchantResponse>>() {}.getType();

        RegisterMerchantResponse data = verifyAPI("/v1/merchant", request, responseType);

        RegisterMerchantResponse expectedResponse = RegisterMerchantResponse.builder()
                .email(request.getEmail())
                .name(request.getName())
                .build();

        assertThat(data).isEqualToIgnoringGivenFields(expectedResponse, "merchantID");

        light.pay.api.response.Response<Wallet> walletResponse = walletRepository.getByUserId(data.getMerchantID());

        assertTrue(walletResponse.isSuccess());
        assertEquals(data.getMerchantID(), walletResponse.getData().getUserID());
        assertEquals(0, walletResponse.getData().getBalance());

        light.pay.api.response.Response<Account> accountResponse = accountRepository.getByUserId(data.getMerchantID());
        assertTrue(accountResponse.isSuccess());

        Account accountResponseData = accountResponse.getData();

        assertEquals(data.getMerchantID(), accountResponseData.getUserID());
        assertEquals(request.getName(), accountResponseData.getName());
        assertEquals(request.getEmail(), accountResponseData.getEmail());
        assertEquals(UserType.MERCHANT, accountResponseData.getUserType());
    }

    @Test
    void shouldTopup() throws IOException {
        RegisterCustomerRequest request = objectGenerator.nextObject(RegisterCustomerRequest.class);
        Type responseType = new TypeToken<light.pay.api.response.Response<RegisterCustomerResponse>>() {}.getType();
        RegisterCustomerResponse customerData = verifyAPI("/v1/customer", request, responseType);

        TopupRequest topupRequest = TopupRequest.builder()
                .referenceID(UUID.randomUUID().toString())
                .amount(100L)
                .userID(customerData.getUserID())
                .description("from-bank")
                .build();
        Type topupResponseType = new TypeToken<light.pay.api.response.Response<TopupResponse>>() {}.getType();
        TopupResponse data = verifyAPI("/v1/topup", topupRequest, topupResponseType);

        TopupResponse expectedResponse = TopupResponse.builder()
                .referenceID(topupRequest.getReferenceID())
                .amount(topupRequest.getAmount())
                .userID(customerData.getUserID())
                .description(topupRequest.getDescription())
                .build();

        assertThat(data).isEqualToIgnoringGivenFields(expectedResponse, "transactionID");

        light.pay.api.response.Response<Wallet> walletResponse = walletRepository.getByUserId(data.getUserID());
        assertTrue(walletResponse.isSuccess());
        assertEquals(customerData.getUserID(), walletResponse.getData().getUserID());
        assertEquals(topupRequest.getAmount(), walletResponse.getData().getBalance());

        light.pay.api.response.Response<Transaction> transactionResponse = transactionRepository.getByTransactionID(data.getTransactionID());
        Transaction transaction = transactionResponse.getData();
        assertEquals(topupRequest.getReferenceID(), transaction.getReferenceID());
        assertEquals(walletResponse.getData().getWalletID(), transaction.getCreditedWallet());
        assertEquals("", transaction.getDebitedWallet());
        assertEquals(data.getTransactionID(), transaction.getTransactionID());
        assertEquals(topupRequest.getDescription(), transaction.getDescription());
        assertEquals(TransactionType.TOPUP, transaction.getTransactionType());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    }

    @Test
    void shouldPay() throws IOException {
        RegisterCustomerRequest request = objectGenerator.nextObject(RegisterCustomerRequest.class);
        Type responseType = new TypeToken<light.pay.api.response.Response<RegisterCustomerResponse>>() {}.getType();
        RegisterCustomerResponse customerData = verifyAPI("/v1/customer", request, responseType);

        TopupRequest topupRequest = TopupRequest.builder()
                .referenceID(UUID.randomUUID().toString())
                .amount(100L)
                .userID(customerData.getUserID())
                .description("from-bank")
                .build();
        Type topupResponseType = new TypeToken<light.pay.api.response.Response<TopupResponse>>() {}.getType();
        verifyAPI("/v1/topup", topupRequest, topupResponseType);

        RegisterMerchantRequest registerMerchantRequest = objectGenerator.nextObject(RegisterMerchantRequest.class);
        Type merchantResponseType = new TypeToken<light.pay.api.response.Response<RegisterMerchantResponse>>() {}.getType();
        RegisterMerchantResponse merchantData = verifyAPI("/v1/merchant", registerMerchantRequest, merchantResponseType);


        PayRequest payRequest = PayRequest.builder()
                .payerId(customerData.getUserID())
                .payeeId(merchantData.getMerchantID())
                .referenceID(UUID.randomUUID().toString())
                .amount(100L)
                .description("some-payment")
                .build();
        Type payResponseType = new TypeToken<light.pay.api.response.Response<PayResponse>>() {}.getType();
        PayResponse payData = verifyAPI("/v1/pay", payRequest, payResponseType);

        PayResponse expectedResponse = PayResponse.builder()
                .payerId(customerData.getUserID())
                .payeeId(merchantData.getMerchantID())
                .referenceID(payRequest.getReferenceID())
                .amount(payRequest.getAmount())
                .description(payRequest.getDescription())
                .build();

        assertThat(payData).isEqualToIgnoringGivenFields(expectedResponse, "transactionID");

        light.pay.api.response.Response<Wallet> payerWalletResponse = walletRepository.getByUserId(payRequest.getPayerId());
        assertTrue(payerWalletResponse.isSuccess());
        assertEquals(customerData.getUserID(), payerWalletResponse.getData().getUserID());
        assertEquals(0L, payerWalletResponse.getData().getBalance());

        light.pay.api.response.Response<Wallet> payeeWalletResponse = walletRepository.getByUserId(payRequest.getPayeeId());
        assertTrue(payeeWalletResponse.isSuccess());
        assertEquals(merchantData.getMerchantID(), payeeWalletResponse.getData().getUserID());
        assertEquals(100L, payeeWalletResponse.getData().getBalance());

        light.pay.api.response.Response<Transaction> transactionResponse = transactionRepository.getByTransactionID(payData.getTransactionID());
        Transaction transaction = transactionResponse.getData();
        assertEquals(payRequest.getReferenceID(), transaction.getReferenceID());
        assertEquals(payeeWalletResponse.getData().getWalletID(), transaction.getCreditedWallet());
        assertEquals(payerWalletResponse.getData().getWalletID(), transaction.getDebitedWallet());
        assertEquals(payData.getTransactionID(), transaction.getTransactionID());
        assertEquals(payRequest.getDescription(), transaction.getDescription());
        assertEquals(TransactionType.PAYMENT, transaction.getTransactionType());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    }

    private <T> T verifyAPI(String path, Object body, Type typeOfT) throws IOException {
        ClientResponse clientResponse = postAPI(path, body);
        assertEquals(200, clientResponse.code);

        light.pay.api.response.Response<T> responseBody = JsonUtils.fromJson(clientResponse.body, typeOfT);
        assertTrue(responseBody.isSuccess());
        return responseBody.getData();
    }

    private ClientResponse postAPI(String path, Object body) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + path)
                .post(RequestBody.create(MediaType.get("application/json"), JsonUtils.toJson(body)))
                .build();

        return executeAPI(request);
    }

    private ClientResponse executeAPI(Request request) throws IOException {
        Response response = okHttpClient.newCall(request).execute();
        ClientResponse clientResponse = new ClientResponse(response.body().string(), response.code());
        response.close();
        return clientResponse;
    }

    @Value
    private static class ClientResponse{
        private String body;
        private int code;
    }
}
