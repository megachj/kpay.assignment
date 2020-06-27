package megachj.kpay.assignment.repository;

import megachj.kpay.assignment.model.entity.DistributedInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributedInfoRepository extends JpaRepository<DistributedInfoEntity, DistributedInfoEntity.Id> {
}
