package megachj.kpay.assignment.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import megachj.kpay.assignment.model.dto.DistributedInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(DistributedInfoEntity.Id.class)
@Table(name = "distributed_info",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"statementId", "userId"}
                )
        })
public class DistributedInfoEntity extends DistributedInfo {

    @Version
    private Integer version;

    public static DistributedInfoEntity newInstance(Long statementId, int no, int amount) {
        DistributedInfoEntity newEntity = new DistributedInfoEntity();
        newEntity.setStatementId(statementId);
        newEntity.setNo(no);
        newEntity.setAmount(amount);
        newEntity.setState(State.NOT_YET);
        newEntity.setUserId(null);
        newEntity.setRecvTimestamp(null);

        return newEntity;
    }

    // unique key
    @Override
    @javax.persistence.Id
    @Column(nullable = false)
    public Long getStatementId() {
        return statementId;
    }

    @Override
    @javax.persistence.Id
    @Column
    public int getNo() {
        return no;
    }

    @Override
    @Column
    public int getAmount() {
        return amount;
    }

    @Override
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public State getState() {
        return state;
    }

    // unique key
    @Override
    @Column
    public Integer getUserId() {
        return userId;
    }

    @Override
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecvTimestamp() {
        return recvTimestamp;
    }

    @Data
    @EqualsAndHashCode
    public static class Id implements Serializable {
        private Long statementId;
        private int no;
    }
}
