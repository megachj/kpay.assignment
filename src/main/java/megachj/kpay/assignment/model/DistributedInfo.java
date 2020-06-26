package megachj.kpay.assignment.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributedInfo {

    protected Long statementId;

    protected int no;

    protected int amount;

    protected boolean isReceived;

    protected int userId;

    protected Date timestamp;
}
