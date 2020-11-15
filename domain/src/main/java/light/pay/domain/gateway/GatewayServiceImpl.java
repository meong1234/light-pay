package light.pay.domain.gateway;

import light.pay.api.accounts.AccountService;
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
import light.pay.api.wallets.WalletService;

public class GatewayServiceImpl implements GatewayService {

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
        return null;
    }

    @Override
    public Response<RegisterMerchantResponse> registerMerchant(RegisterMerchantRequest request) {
        return null;
    }

    @Override
    public Response<TopupResponse> topup(TopupRequest request) {
        return null;
    }

    @Override
    public Response<PayResponse> pay(PayRequest request) {
        return null;
    }
}
