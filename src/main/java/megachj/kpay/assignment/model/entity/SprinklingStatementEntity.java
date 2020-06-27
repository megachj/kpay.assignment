package megachj.kpay.assignment.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import megachj.kpay.assignment.model.dto.SprinklingStatement;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<DistributedInfoEntity> distributedInfoEntityList = new ArrayList<>();

    public static SprinklingStatementEntity newInstance(String token, String roomId, int userId, int amount, int distributedNumber, long tokenValidMinutes) {
        LocalDateTime now = LocalDateTime.now();

        SprinklingStatementEntity newEntity = new SprinklingStatementEntity();
        newEntity.setToken(token);
        newEntity.setRoomId(roomId);
        newEntity.setUserId(userId);
        newEntity.setAmount(amount);
        newEntity.setDistributedNumber(distributedNumber);
        newEntity.setRegDate(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
        newEntity.setExpiredDate(Date.from(now.plusMinutes(tokenValidMinutes).atZone(ZoneId.systemDefault()).toInstant()));

        return newEntity;
    }

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "statementId")
    public List<DistributedInfoEntity> getDistributedInfoEntityList() {
        return distributedInfoEntityList;
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
