package megachj.kpay.assignment.model.rest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ReceiveMoneyResponse extends RestResponse {

    private int receivedAmount;

    public ReceiveMoneyResponse(RestResponseHeader header, int receivedAmount) {
        super(header);
        this.receivedAmount = receivedAmount;
    }
}
