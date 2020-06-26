package megachj.kpay.assignment.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import megachj.kpay.assignment.model.SprinklingStatement;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "sprinkling_statement",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"token", "roomId"}
                )
        })
public class SprinklingStatementEntity extends SprinklingStatement {

    @Setter
    private Set<DistributedInfoEntity> distributedInfoEntitySet;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "statementId")
    public Set<DistributedInfoEntity> getDistributedInfoEntitySet() {
        return distributedInfoEntitySet;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getStatementId() {
        return statementId;
    }

    // unique key
    @Override
    @Column(nullable = false, length = 3)
    public String getToken() {
        return token;
    }

    // unique key
    @Override
    @Column(nullable = false)
    public String getRoomId() {
        return roomId;
    }

    @Override
    @Column
    public int getUserId() {
        return userId;
    }

    @Override
    @Column(nullable = false)
    public int getAmount() {
        return amount;
    }

    @Override
    @Column
    public int getDistributedNumber() {
        return distributedNumber;
    }

    @Override
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRegDate() {
        return regDate;
    }

    @Override
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpiredDate() {
        return expiredDate;
    }
}
