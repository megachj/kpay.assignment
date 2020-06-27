package megachj.kpay.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.constant.ResultCodes;
import megachj.kpay.assignment.exception.SprinklingException;
import megachj.kpay.assignment.model.dto.DistributedInfo;
import megachj.kpay.assignment.model.entity.DistributedInfoEntity;
import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import megachj.kpay.assignment.model.dto.SprinklingInfo;
import megachj.kpay.assignment.repository.DistributedInfoRepository;
import megachj.kpay.assignment.repository.SprinklingStatementRepository;
import megachj.kpay.assignment.utils.RandomGenerator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MoneySprinklingService {

    private static final long TOKEN_VALID_MINUTES = 10;

    private static final int TOKEN_LENGTH = 3;

    private static final int SEARCH_LIMIT_DAYS = 7;

    private final SprinklingStatementRepository sprinklingStatementRepository;

    private final DistributedInfoRepository distributedInfoRepository;

    /**
     * 돈 뿌리기 등록
     *
     * @param userId
     * @param roomId
     * @param amount
     * @param distributedNumber
     * @return token
     * @throws IllegalArgumentException
     * @throws DataIntegrityViolationException: [roomId, token] 이 중복될때 발생. token 이 3자리 문자열이라 중복 가능성이 있음.
     */
    @Transactional
    public String addMoneySprinkling(int userId, String roomId, int amount, int distributedNumber) throws IllegalArgumentException, DataIntegrityViolationException {
        if (distributedNumber <= 0 || amount < distributedNumber) {
            throw new IllegalArgumentException("The condition was not satisfied. 0 < distributedNumber <= amount");
        }

        String token = RandomGenerator.randomString(TOKEN_LENGTH);
        SprinklingStatementEntity entity = SprinklingStatementEntity.newInstance(token, roomId, userId, amount, distributedNumber, TOKEN_VALID_MINUTES);
        try {
            sprinklingStatementRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            // [roomId, token] 중복인 경우 1번 Retry, 또 중복 발생하면 예외 발생.
            // 최소 7일이 지난 만료된 뿌리기 정보를 배치로 지우는 작업을 해줘야 중복 가능성 낮아짐.
            // 또는 token 문자열이 길어져야 중복 가능성 낮아짐.
            token = RandomGenerator.randomString(TOKEN_LENGTH);
            entity = SprinklingStatementEntity.newInstance(token, roomId, userId, amount, distributedNumber, TOKEN_VALID_MINUTES);
            sprinklingStatementRepository.save(entity);
        }

        /*
        NOTE: 벌크 삽입이 되지 않음. 벌크 삽입을 하려면 version 필드 값을 강제로 증가시켜야 하고, 그러기 위해선 특별한 락 옵션을 선택해야한다.
          - 뿌리는 인원이 많다면 성능 이슈가 있을 수 있음. 보통은 카톡 방은 소수 인원이므로 크게 문제되지 않을 것으로 보임.
          - 정확히 하기 위해선 성능 테스트 필요.
         */
        List<DistributedInfoEntity> distributedInfoEntities = new ArrayList<>(distributedNumber);
        List<Integer> randomDistributedMoney = RandomGenerator.randomDistribution(amount, distributedNumber);
        for (int idx = 0; idx < randomDistributedMoney.size(); ++idx) {
            distributedInfoEntities.add(DistributedInfoEntity.newInstance(entity.getStatementId(), idx, randomDistributedMoney.get(idx)));
        }
        distributedInfoRepository.save(distributedInfoEntities);

        log.info("userId: {}, roomId: {}, amount: {}, distributedNumber: {}, token: {}", userId, roomId, amount, distributedNumber, token);

        return token;
    }

    /**
     * 뿌린 돈 받아가기
     *
     * @param userId
     * @param roomId
     * @param token
     * @return receivedMoney
     * @throws SprinklingException: API 예외 조건에 따라 발생.
     * @throws ObjectOptimisticLockingFailureException: 여러 유저가 동시에 돈을 받을 때 늦게 갱신하는 유저에게 낙관적 락 예외 발생.
     */
    @Transactional
    public int receiveMoney(int userId, String roomId, String token) throws SprinklingException, ObjectOptimisticLockingFailureException {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = sprinklingStatementRepository.findDetailEntity(roomId, token);

        verifyReceiveMoneyPreCondition(entity, now, userId);

        DistributedInfoEntity distributedInfoEntity = entity.getDistributedInfoEntityList()
                .stream()
                .filter(v -> v.getState() == DistributedInfo.State.NOT_YET)
                .findAny()
                .orElseThrow(RuntimeException::new);

        distributedInfoEntity.setState(DistributedInfo.State.DONE);
        distributedInfoEntity.setUserId(userId);
        distributedInfoEntity.setRecvTimestamp(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));

        log.info("userId: {}, roomId: {}, token: {}", userId, roomId, token);

        return distributedInfoEntity.getAmount();
    }

    private void verifyReceiveMoneyPreCondition(SprinklingStatementEntity entity, LocalDateTime now, int userId) {
        // 데이터 없음
        if (entity == null) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.DATA_NOT_FOUND.getCode(), ResultCodes.DATA_NOT_FOUND.getDefaultMessage());
        }

        // 토큰 만료
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

    /**
     * 돈 뿌린 내역 조회
     *
     * @param userId
     * @param roomId
     * @param token
     * @return sprinklingInfo
     * @throws SprinklingException
     */
    @Transactional(readOnly = true)
    public SprinklingInfo getSprinklingInfo(int userId, String roomId, String token) throws SprinklingException {
        LocalDateTime now = LocalDateTime.now();
        SprinklingStatementEntity entity = sprinklingStatementRepository.findDetailEntity(roomId, token);

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

        // 조회 기간 만료
        LocalDateTime regDate = LocalDateTime.ofInstant(entity.getRegDate().toInstant(), ZoneId.systemDefault());
        if (now.minusDays(SEARCH_LIMIT_DAYS).isAfter(regDate)) {
            throw new SprinklingException(System.currentTimeMillis(), ResultCodes.SEARCH_PERIOD_EXPIRATION.getCode(), ResultCodes.SEARCH_PERIOD_EXPIRATION.getDefaultMessage());
        }
    }
}
