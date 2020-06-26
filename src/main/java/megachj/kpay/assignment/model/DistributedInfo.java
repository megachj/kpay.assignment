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

    protected State state;

    protected int userId;

    protected Date recvTimestamp;

    public enum State {
        // 받기 완료
        DONE,

        // 아직 받지 않은 상태
        NOT_YET;
    }
}
