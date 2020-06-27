package megachj.kpay.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.constant.ResultCodes;
import megachj.kpay.assignment.exception.SprinklingException;
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

    private static final int SEARCH_LIMIT_DAYS = 7;

    private final MoneySprinklingRepository moneySprinklingRepository;

    private final DistributedInfoRepository distributedInfoRepository;

    @Transactional
    public String addMoneySprinkling(int userId, String roomId, int amount, int distributedNumber) throws IllegalArgumentException, SprinklingException {
        if (distributedNumber <= 0 || amount < distributedNumber) {
            throw new IllegalArgumentException("The condition was not satisfied. 0 < distributedNumber <= amount");
        }

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
    public int receiveMoney(int userId, String roomId, String token) throws SprinklingException {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = moneySprinklingRepository.findDetailEntity(roomId, token);

        verifyReceiveMoneyPreCondition(entity, now, userId);

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

    private void verifyReceiveMoneyPreCondition(SprinklingStatementEntity entity, LocalDateTime now, int userId) {
        // 데이터 없음
        if (entity == null) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.DATA_NOT_FOUND.getCode(), ResultCodes.DATA_NOT_FOUND.getDefaultMessage());
        }

        // token 만료
        LocalDateTime expiredDate = LocalDateTime.ofInstant(entity.getExpiredDate().toInstant(), ZoneId.systemDefault());
        if (!now.isBefore(expiredDate)) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.EXPIRED_TOKEN.getCode(), ResultCodes.EXPIRED_TOKEN.getDefaultMessage());
        }

        // 뿌린 유저
        if (entity.getUserId() == userId) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.INVALID_USER.getCode(), "The user who sprinkled the money cannot accept it.");
        }

        // 이미 한 번 받은 유저
        if (entity.getDistributedInfoEntityList().stream().filter(v -> v.getState() == DistributedInfo.State.DONE).anyMatch(v -> v.getUserId() == userId)) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.INVALID_USER.getCode(), "The user has already received the money.");
        }

        // 모두 받기 완료된 경우
        if (entity.getDistributedInfoEntityList().stream().allMatch(v -> v.getState() == DistributedInfo.State.DONE)) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.MONEY_RUN_OUT.getCode(), ResultCodes.MONEY_RUN_OUT.getDefaultMessage());
        }
    }

    @Transactional(readOnly = true)
    public SprinklingInfo getSprinklingInfo(int userId, String roomId, String token) throws SprinklingException {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = moneySprinklingRepository.findDetailEntity(roomId, token);

        verifyGetSprinklingInfoPreCondition(entity, now, userId);

        return SprinklingInfo.of(entity);
    }

    private void verifyGetSprinklingInfoPreCondition(SprinklingStatementEntity entity, LocalDateTime now, int userId) {
        // 데이터 없음
        if (entity == null) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.DATA_NOT_FOUND.getCode(), ResultCodes.DATA_NOT_FOUND.getDefaultMessage());
        }

        // 뿌린 유저가 아님
        if (entity.getUserId() != userId) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.INVALID_USER.getCode(), "Only user who sprinkle money can search it.");
        }

        // SEARCH_LIMIT_DAYS 지난 데이터는 조회할 수 없음
        LocalDateTime regDate = LocalDateTime.ofInstant(entity.getRegDate().toInstant(), ZoneId.systemDefault());
        if (now.minusDays(SEARCH_LIMIT_DAYS).isAfter(regDate)) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.SEARCH_PERIOD_EXPIRATION.getCode(), ResultCodes.SEARCH_PERIOD_EXPIRATION.getDefaultMessage());
        }
    }
}
