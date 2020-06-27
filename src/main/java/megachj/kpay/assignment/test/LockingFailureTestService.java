package megachj.kpay.assignment.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.model.dto.DistributedInfo;
import megachj.kpay.assignment.model.entity.DistributedInfoEntity;
import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import megachj.kpay.assignment.repository.SprinklingStatementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * NOTE: 테스트 코드를 위해 만든 클래스로 실제 코드에서는 사용하면 안된다.
 *       테스트 코드 실행하지 않을 땐 @Service 어노테이션을 주석처리 해서, 사용을 막자.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LockingFailureTestService {

    private final SprinklingStatementRepository sprinklingStatementRepository;

    /**
     * ObjectOptimisticLockingFailureException: 여러 유저가 동시에 돈을 받을 때 늦게 갱신하는 유저에게 낙관적 락 예외 발생 테스트를 위한 메서드
     *
     * @param userId
     * @param roomId
     * @param token
     */
    @Transactional
    public void receiveMoneyForLockingExceptionTest(int userId, String roomId, String token) {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = sprinklingStatementRepository.findDetailEntity(roomId, token);

        DistributedInfoEntity distributedInfoEntity = entity.getDistributedInfoEntityList()
                .stream()
                .filter(v -> v.getState() == DistributedInfo.State.NOT_YET)
                .findAny()
                .orElseThrow(RuntimeException::new);

        // 조회후 일정시간 스레드를 sleep 해서 동시에 갱신이 일어나도록 만든다.
        try {
            Thread.sleep(2000);
        } catch (Exception ignored) {}

        distributedInfoEntity.setState(DistributedInfo.State.DONE);
        distributedInfoEntity.setUserId(userId);
        distributedInfoEntity.setRecvTimestamp(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
    }
}
