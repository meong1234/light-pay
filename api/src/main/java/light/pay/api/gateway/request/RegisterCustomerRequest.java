package light.pay.api.gateway.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterCustomerRequest {
    private String name;
    private String email;
    private String phoneNumber;
}
