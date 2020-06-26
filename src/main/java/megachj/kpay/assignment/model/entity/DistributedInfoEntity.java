package megachj.kpay.assignment.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import megachj.kpay.assignment.model.DistributedInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(DistributedInfoEntity.Id.class)
@Table(name = "distributed_info")
public class DistributedInfoEntity extends DistributedInfo {

    @Version
    private Integer version;

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

    @Override
    @Column
    public int getUserId() {
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
