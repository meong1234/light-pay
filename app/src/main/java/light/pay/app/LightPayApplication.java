package light.pay.app;

import light.pay.api.gateway.GatewayService;
import light.pay.domain.DomainFactory;
import light.pay.server.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LightPayApplication {
    private static final Logger logger = LoggerFactory.getLogger(LightPayApplication.class);

    public static void main(String[] args) {
        logger.info("Starting monolith application");

        GatewayService gatewayService = DomainFactory.createService();
        Router router = new Router(gatewayService);
        router.configure();

        logger.info("server is running");

        Runtime.getRuntime().addShutdownHook(new Thread(router::stopServer));
    }
}
