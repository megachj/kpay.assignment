package megachj.kpay.assignment.model.entity;

import lombok.*;
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

    @Setter
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

    /*
    NOTE: 두 번의 갱신 분실 문제(second lost updates problem) 해결을 위한 필드
      - userA, userB 가 동시에 돈을 받아가는 문제가 발생할 수 있음.
      - 최초 커밋만 인정: userA, userB 중 먼저 갱신한 커밋만 인정하고, 나중에 갱신하는 유저에는 낙관적 락 예외 발생.
        org.springframework.orm.ObjectOptimisticLockingFailureException
     */
    @Version
    @Column
    public Integer getVersion() {
        return version;
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
