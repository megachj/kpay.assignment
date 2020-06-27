package megachj.kpay.assignment.model.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import megachj.kpay.assignment.model.dto.SprinklingInfo;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SprinklingInfoResponse extends RestResponse {

    private SprinklingInfo sprinklingInfo;

    public SprinklingInfoResponse(RestResponseHeader header, SprinklingInfo sprinklingInfo) {
        super(header);
        this.sprinklingInfo = sprinklingInfo;
    }
}
