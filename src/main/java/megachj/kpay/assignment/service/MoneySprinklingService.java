package megachj.kpay.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.model.dto.DistributedInfo;
import megachj.kpay.assignment.model.entity.DistributedInfoEntity;
import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import megachj.kpay.assignment.model.rest.SprinklingInfo;
import megachj.kpay.assignment.repository.DistributedInfoRepository;
import megachj.kpay.assignment.repository.MoneySprinklingRepository;
import megachj.kpay.assignment.utils.RandomGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MoneySprinklingService {

    private final MoneySprinklingRepository moneySprinklingRepository;

    private final DistributedInfoRepository distributedInfoRepository;

    /*
    에러 경우의 수:
      - 데이터 저장 실패, <roomId, token> 중복, token 발급 재시도, 2번 실패하면 exception 발생
     */
    @Transactional
    public String addMoneySprinkling(int userId, String roomId, int amount, int distributedNumber) {

        String token = UUID.randomUUID().toString().substring(0, 3);

        SprinklingStatementEntity entity = SprinklingStatementEntity.newInstance(token, roomId, userId, amount, distributedNumber);
        moneySprinklingRepository.save(entity);

        List<DistributedInfoEntity> distributedInfoEntities = new ArrayList<>(distributedNumber);
        List<Integer> randomDistributedMoney = RandomGenerator.randomDistribution(amount, distributedNumber);
        for (int idx = 0; idx < randomDistributedMoney.size(); ++idx) {
            distributedInfoEntities.add(DistributedInfoEntity.newInstance(entity.getStatementId(), idx, randomDistributedMoney.get(idx)));
        }
        distributedInfoRepository.save(distributedInfoEntities);

        return token;
    }

    @Transactional
    public int receiveMoney(int userId, String roomId, String token) {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = moneySprinklingRepository.findDetailEntity(roomId, token);

        checkReceiveMoneyPreCondition(entity, now, userId);

        DistributedInfoEntity distributedInfoEntity = entity.getDistributedInfoEntityList()
                .stream()
                .filter(v -> v.getState() == DistributedInfo.State.NOT_YET)
                .findAny()
                .orElseThrow(RuntimeException::new);

        distributedInfoEntity.setState(DistributedInfo.State.DONE);
        distributedInfoEntity.setUserId(userId);
        distributedInfoEntity.setRecvTimestamp(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        return distributedInfoEntity.getAmount();
    }

    private void checkReceiveMoneyPreCondition(SprinklingStatementEntity entity, LocalDateTime now, int userId) {
        // 데이터 없음
        if (entity == null) {
            throw new RuntimeException();
        }

        // token 만료
        LocalDateTime expiredDate = LocalDateTime.ofInstant(entity.getExpiredDate().toInstant(), ZoneId.systemDefault());
        if (!now.isBefore(expiredDate)) {
            throw new RuntimeException();
        }

        // 뿌린 유저
        if (entity.getUserId() == userId) {
            throw new RuntimeException();
        }

        // 이미 한 번 받은 유저
        if (entity.getDistributedInfoEntityList().stream().filter(v -> v.getState() == DistributedInfo.State.DONE).anyMatch(v -> v.getUserId() == userId)) {
            throw new RuntimeException();
        }

        // 모두 받기 완료된 경우
        if (entity.getDistributedInfoEntityList().stream().allMatch(v -> v.getState() == DistributedInfo.State.DONE)) {
            throw new RuntimeException();
        }
    }

    @Transactional(readOnly = true)
    public SprinklingInfo getSprinklingInfo(int userId, String roomId, String token) {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = moneySprinklingRepository.findDetailEntity(roomId, token);

        checkGetSprinklingInfoPreCondition(entity, userId);

        // 7일이 지난 조회는 null 리턴
        LocalDateTime regDate = LocalDateTime.ofInstant(entity.getRegDate().toInstant(), ZoneId.systemDefault());
        if (now.minusDays(7).isAfter(regDate)) {
            return null;
        }

        return SprinklingInfo.of(entity);
    }

    private void checkGetSprinklingInfoPreCondition(SprinklingStatementEntity entity, int userId) {
        // 데이터 없음
        if (entity == null) {
            throw new RuntimeException();
        }

        // 뿌린 유저가 아님
        if (entity.getUserId() != userId) {
            throw new RuntimeException();
        }
    }
}
