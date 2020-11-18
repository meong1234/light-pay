package light.pay.api.gateway;

import light.pay.api.response.Response;
import light.pay.api.gateway.request.PayRequest;
import light.pay.api.gateway.request.RegisterCustomerRequest;
import light.pay.api.gateway.request.RegisterMerchantRequest;
import light.pay.api.gateway.request.TopupRequest;
import light.pay.api.gateway.response.PayResponse;
import light.pay.api.gateway.response.RegisterCustomerResponse;
import light.pay.api.gateway.response.RegisterMerchantResponse;
import light.pay.api.gateway.response.TopupResponse;

public interface GatewayService {
    Response<RegisterCustomerResponse> registerCustomer(RegisterCustomerRequest request);
    Response<RegisterMerchantResponse> registerMerchant(RegisterMerchantRequest request);
    Response<TopupResponse> topup(TopupRequest request);
    Response<PayResponse> pay(PayRequest request);
}
