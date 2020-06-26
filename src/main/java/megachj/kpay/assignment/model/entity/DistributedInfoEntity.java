//package megachj.kpay.assignment.model.entity;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.ToString;
//import megachj.kpay.assignment.model.DistributedInfo;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//
//@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
//@Entity
//@IdClass(DistributedInfoEntity.Id.class)
//@Table(name = "distributed_info")
//public class DistributedInfoEntity extends DistributedInfo {
//
//    @Version
//    private Integer version;
//
//    @Override
//    @Column(nullable = false)
//    public Long getStatementId() {
//        return statementId;
//    }
//
//    @Override
//    @Column
//    public int getNo() {
//        return no;
//    }
//
//    @Override
//    @Column
//    public int getAmount() {
//        return amount;
//    }
//
//    @Override
//    @Column
//    public boolean isReceived() {
//        return isReceived;
//    }
//
//    @Override
//    @Column
//    public int getUserId() {
//        return userId;
//    }
//
//    @Override
//    @Column
//    public Date getTimestamp() {
//        return timestamp;
//    }
//
//    @Data
//    @EqualsAndHashCode
//    public static class Id implements Serializable {
//        private Long statementId;
//        private int no;
//    }
//}
