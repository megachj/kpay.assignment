package megachj.kpay.assignment.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SprinklingInfoResponse {

    private ResponseHeader header;

    private SprinklingInfo sprinklingInfo;
}
