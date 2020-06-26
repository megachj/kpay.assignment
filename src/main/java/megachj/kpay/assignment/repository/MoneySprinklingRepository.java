package megachj.kpay.assignment.repository;

import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneySprinklingRepository extends JpaRepository<SprinklingStatementEntity, Long> {

}
