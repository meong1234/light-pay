package light.pay.api.errors;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Error {
    private String entity;
    private String code;
    private String cause;
}
