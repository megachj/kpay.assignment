package megachj.kpay.assignment.repository;

import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SprinklingStatementRepository extends JpaRepository<SprinklingStatementEntity, Long> {

    @Query(
            " SELECT s "
            + " FROM SprinklingStatementEntity s "
            + " JOIN FETCH s.distributedInfoEntityList d "
            + " WHERE s.roomId = :roomId AND s.token = :token "
    )
    SprinklingStatementEntity findDetailEntity(@Param("roomId") String roomId, @Param("token") String token);

    SprinklingStatementEntity findByRoomIdAndToken(@Param("roomId") String roomId, @Param("token") String token);
}
