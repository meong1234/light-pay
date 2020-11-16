package light.pay.api.errors;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Error {
    private String entity;
    private String code;
    private String cause;

    public Error(String entity, String code, String cause) {
        this.entity = entity;
        this.code = code;
        this.cause = cause;
    }
}
