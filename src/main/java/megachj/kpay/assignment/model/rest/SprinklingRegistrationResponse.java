package megachj.kpay.assignment.model.rest;

import lombok.*;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SprinklingRegistrationResponse extends RestResponse {

    private String token;

    public SprinklingRegistrationResponse(RestResponseHeader header, String token) {
        super(header);
        this.token = token;
    }
}
