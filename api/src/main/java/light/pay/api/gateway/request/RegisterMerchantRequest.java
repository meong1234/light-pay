package light.pay.api.gateway.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterMerchantRequest {
    private String name;
    private String email;
}
