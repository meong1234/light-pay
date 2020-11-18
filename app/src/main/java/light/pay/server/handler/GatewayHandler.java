package light.pay.server.handler;

import light.pay.api.gateway.GatewayService;
import light.pay.api.gateway.request.PayRequest;
import light.pay.api.gateway.request.RegisterCustomerRequest;
import light.pay.api.gateway.request.RegisterMerchantRequest;
import light.pay.api.gateway.request.TopupRequest;
import light.pay.api.gateway.response.PayResponse;
import light.pay.api.gateway.response.RegisterCustomerResponse;
import light.pay.api.gateway.response.RegisterMerchantResponse;
import light.pay.api.gateway.response.TopupResponse;
import light.pay.commons.marshalling.JsonUtils;
import spark.Request;
import spark.Response;

public class GatewayHandler {
    private GatewayService gatewayService;

    public GatewayHandler(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    public light.pay.api.errors.Response<RegisterCustomerResponse> registerCustomer(Request request, Response response) {
        RegisterCustomerRequest reqBody = JsonUtils
                .fromJson(request.body(), RegisterCustomerRequest.class);

        return gatewayService.registerCustomer(reqBody);
    }

    public light.pay.api.errors.Response<RegisterMerchantResponse> registerMerchant(Request request, Response response) {
        RegisterMerchantRequest reqBody = JsonUtils
                .fromJson(request.body(), RegisterMerchantRequest.class);

        return gatewayService.registerMerchant(reqBody);
    }

    public light.pay.api.errors.Response<TopupResponse> topup(Request request, Response response) {
        TopupRequest reqBody = JsonUtils
                .fromJson(request.body(), TopupRequest.class);

        return gatewayService.topup(reqBody);
    }

    public light.pay.api.errors.Response<PayResponse> pay(Request request, Response response) {
        PayRequest reqBody = JsonUtils
                .fromJson(request.body(), PayRequest.class);

        return gatewayService.pay(reqBody);
    }
}
