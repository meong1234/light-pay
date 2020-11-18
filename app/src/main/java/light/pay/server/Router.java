package light.pay.server;

import com.gojek.ApplicationConfiguration;
import com.gojek.Figaro;
import light.pay.api.errors.Errors;
import light.pay.api.response.Response;
import light.pay.api.gateway.GatewayService;
import light.pay.commons.marshalling.JsonUtils;
import light.pay.server.handler.GatewayHandler;
import light.pay.server.transformer.JsonTransformer;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static spark.Spark.*;

public class Router {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    private GatewayHandler gatewayHandler;

    private ApplicationConfiguration configuration;

    public Router(GatewayService gatewayService) {
        this.gatewayHandler = new GatewayHandler(gatewayService);
        this.configuration = Figaro.configure(new HashSet<>(Arrays.asList(
                "PORT"
        )));
    }

    public void configure() {
        int apiPort = configuration.getValueAsInt("PORT");
        port(apiPort);

        get("/ping", (req, resp) -> "pong");

        before("/v1/*", (request, response) -> {
            MDC.clear();
            MDC.put("correlation_id", UUID.randomUUID().toString());
        });

        path("/v1", () -> {
            post("/customer", gatewayHandler::registerCustomer, new JsonTransformer());
            post("/merchant", gatewayHandler::registerMerchant, new JsonTransformer());
            post("/topup", gatewayHandler::topup, new JsonTransformer());
            post("/pay", gatewayHandler::pay, new JsonTransformer());
        });

        after((request, response) -> response.type("application/json"));

        exception(Exception.class, (exception, request, response) -> {
            logger.error("unknown error", exception);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.body(JsonUtils.toJson(Response.createErrorResponse(Errors.GENERIC_ERROR_CODE, "", "")));
        });
    }

    public void stopServer() {
        stop();
        awaitStop();
    }
}
