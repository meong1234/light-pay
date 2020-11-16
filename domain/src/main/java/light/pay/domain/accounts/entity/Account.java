package light.pay.domain.accounts.entity;

import light.pay.api.accounts.UserType;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class Account {
    private Long id;
    private String userID;
    private String name;
    private String email;
    private String phoneNumber;
    private UserType userType;
}
