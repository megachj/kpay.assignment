package megachj.kpay.assignment.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SprinklingRegistrationResponse {

    private ResponseHeader header;

    private String token;
}
