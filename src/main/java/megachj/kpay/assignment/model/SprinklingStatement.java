package megachj.kpay.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprinklingStatement {

    protected Long statementId;

    @NotEmpty
    protected String token;

    @NotEmpty
    protected String roomId;

    protected int userId;

    protected int amount;

    protected int distributedNumber;

    @NotNull
    protected Date regDate;

    @NotNull
    protected Date expiredDate;
}
