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
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GatewayHandlerTest {

    private GatewayHandler gatewayHandler;
    private GatewayService mockGatewayService;
    private EasyRandom objectGenerator = new EasyRandom();
    private Request mockRequest;
    private Response mockResponse;


    @BeforeEach
    void setUp() {
        mockGatewayService = mock(GatewayService.class);
        gatewayHandler = new GatewayHandler(mockGatewayService);

        mockRequest = mock(Request.class);
        mockResponse = mock(Response.class);
    }

    @Test
    void shouldRegisterCustomer() {
        RegisterCustomerRequest registerCustomerRequest = objectGenerator
                .nextObject(RegisterCustomerRequest.class);

        RegisterCustomerResponse registerCustomerResponse = objectGenerator
                .nextObject(RegisterCustomerResponse.class);

        light.pay.api.errors.Response<RegisterCustomerResponse> successResponse = light.pay.api.errors.Response
                .createSuccessResponse(registerCustomerResponse);

        String body = JsonUtils.toJson(registerCustomerRequest);
        when(mockRequest.body()).thenReturn(body);
        when(mockGatewayService.registerCustomer(eq(registerCustomerRequest)))
                .thenReturn(successResponse);

        light.pay.api.errors.Response<RegisterCustomerResponse> registerCustomerResponseResponse = gatewayHandler
                .registerCustomer(mockRequest, mockResponse);

        assertEquals(successResponse, registerCustomerResponseResponse);

        verify(mockRequest, times(1)).body();
        verify(mockGatewayService, times(1)).registerCustomer(eq(registerCustomerRequest));
    }

    @Test
    void shouldRegisterMerchant() {
        RegisterMerchantRequest registerMerchantRequest = objectGenerator
                .nextObject(RegisterMerchantRequest.class);

        RegisterMerchantResponse registerMerchantResponse = objectGenerator
                .nextObject(RegisterMerchantResponse.class);

        light.pay.api.errors.Response<RegisterMerchantResponse> successResponse = light.pay.api.errors.Response
                .createSuccessResponse(registerMerchantResponse);

        String body = JsonUtils.toJson(registerMerchantRequest);
        when(mockRequest.body()).thenReturn(body);
        when(mockGatewayService.registerMerchant(eq(registerMerchantRequest)))
                .thenReturn(successResponse);

        light.pay.api.errors.Response<RegisterMerchantResponse> registerCustomerResponseResponse = gatewayHandler
                .registerMerchant(mockRequest, mockResponse);

        assertEquals(successResponse, registerCustomerResponseResponse);

        verify(mockRequest, times(1)).body();
        verify(mockGatewayService, times(1)).registerMerchant(eq(registerMerchantRequest));
    }

    @Test
    void shouldTopUp() {
        TopupRequest request = objectGenerator
                .nextObject(TopupRequest.class);

        TopupResponse expectedResponse = objectGenerator
                .nextObject(TopupResponse.class);

        light.pay.api.errors.Response<TopupResponse> successResponse = light.pay.api.errors.Response
                .createSuccessResponse(expectedResponse);

        String body = JsonUtils.toJson(request);
        when(mockRequest.body()).thenReturn(body);
        when(mockGatewayService.topup(eq(request)))
                .thenReturn(successResponse);

        light.pay.api.errors.Response<TopupResponse> gatewayResponse = gatewayHandler
                .topup(mockRequest, mockResponse);

        assertEquals(successResponse, gatewayResponse);

        verify(mockRequest, times(1)).body();
        verify(mockGatewayService, times(1)).topup(eq(request));
    }

    @Test
    void shouldPay() {
        PayRequest request = objectGenerator
                .nextObject(PayRequest.class);

        PayResponse expectedResponse = objectGenerator
                .nextObject(PayResponse.class);

        light.pay.api.errors.Response<PayResponse> successResponse = light.pay.api.errors.Response
                .createSuccessResponse(expectedResponse);

        String body = JsonUtils.toJson(request);
        when(mockRequest.body()).thenReturn(body);
        when(mockGatewayService.pay(eq(request)))
                .thenReturn(successResponse);

        light.pay.api.errors.Response<PayResponse> gatewayResponse = gatewayHandler
                .pay(mockRequest, mockResponse);

        assertEquals(successResponse, gatewayResponse);

        verify(mockRequest, times(1)).body();
        verify(mockGatewayService, times(1)).pay(eq(request));
    }
}