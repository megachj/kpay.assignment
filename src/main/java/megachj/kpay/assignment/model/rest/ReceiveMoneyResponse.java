package megachj.kpay.assignment.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiveMoneyResponse {

    private ResponseHeader header;

    private int receivedAmount;
}
