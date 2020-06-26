package megachj.kpay.assignment.model.dto;

import lombok.*;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SprinklingStatementDetail extends SprinklingStatementSingle {

    private List<DistributedInfo> distributedInfoList;
}
