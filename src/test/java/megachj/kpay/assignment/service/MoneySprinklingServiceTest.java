package megachj.kpay.assignment.service;

import megachj.kpay.assignment.constant.ResultCodes;
import megachj.kpay.assignment.exception.SprinklingException;
import megachj.kpay.assignment.model.dto.SprinklingInfo;
import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;
import megachj.kpay.assignment.repository.SprinklingStatementRepository;
import megachj.kpay.assignment.test.LockingFailureTestService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("local")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoneySprinklingServiceTest {

    @Autowired
    private MoneySprinklingService moneySprinklingService;

    @Autowired
    private LockingFailureTestService lockingFailureTestService;

    @Autowired
    private SprinklingStatementRepository sprinklingStatementRepository;

    private int user1, user2, user3, user4;

    private String room1, room2;

    int amount, distributedNumber;

    @Before
    public void init() {
        user1 = 1;
        user2 = 2;
        user3 = 3;
        user4 = 4;
        room1 = "room1";
        room2 = "room2";
        amount = 10000;
        distributedNumber = 2;
    }

    @Test
    public void success_test() {
        SprinklingInfo sprinklingInfo;

        // user1 이 room1 에 2명에게 10,000원 뿌리기
        String token = moneySprinklingService.addMoneySprinkling(user1, room1, amount, distributedNumber);
        sprinklingInfo = moneySprinklingService.getSprinklingInfo(user1, room1, token);
        assertThat(sprinklingInfo.getRegistrationAmount(), is(amount));
        assertThat(sprinklingInfo.getTotalReceivedAmount(), is(0));

        // user2 돈 받기
        int user2ReceivedMoney = moneySprinklingService.receiveMoney(user2, room1, token);
        sprinklingInfo = moneySprinklingService.getSprinklingInfo(user1, room1, token);
        assertThat(user2ReceivedMoney, is(sprinklingInfo.getTotalReceivedAmount()));
        assertThat(sprinklingInfo.getReceiveInfoList().size(), is(1));

        // user3 돈 받기
        int user3ReceivedMoney = moneySprinklingService.receiveMoney(user3, room1, token);
        sprinklingInfo = moneySprinklingService.getSprinklingInfo(user1, room1, token);
        assertThat(user2ReceivedMoney + user3ReceivedMoney, is(sprinklingInfo.getTotalReceivedAmount()));
        assertThat(sprinklingInfo.getReceiveInfoList().size(), is(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMoneySprinkling_exception_test() {
        moneySprinklingService.addMoneySprinkling(user1, room1, 100, 101);
    }

    /**
     * 여러 유저가 동시에 돈을 받을 때 늦게 갱신하는 유저에게 낙관적 락 예외 발생 테스트
     */
    @Test
    public void receiveMoney_secondLostUpdateProblem_test() {
        // user1 이 room1 에 1명에게 10,000원 뿌리기
        String token = moneySprinklingService.addMoneySprinkling(user1, room1, amount, 1);

        // user2, user3, user4 동시에 갱신
        List<Integer> userList = Arrays.asList(user2, user3, user4);
        List<Integer> successUserList = userList
                .parallelStream()
                .filter(userN -> {
                    try {
                        lockingFailureTestService.receiveMoneyForLockingExceptionTest(userN, room1, token);
                    } catch (ObjectOptimisticLockingFailureException ex) {
                        System.out.printf("fail userId: %d\n", userN);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        assertThat(successUserList.size(), is(1));
        System.out.printf("success userId: %d\n", successUserList.get(0));
    }

    @Test
    public void receiveMoney_exception_test() {
        int exceptionCount = 0;

        // user1 이 room1 에 2명에게 10,000원 뿌리기
        String token = moneySprinklingService.addMoneySprinkling(user1, room1, amount, distributedNumber);
        String invalidToken = "ABCDEFG";

        // 데이터 없음
        try {
            // invalid token
            moneySprinklingService.receiveMoney(user2, room1, invalidToken);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.DATA_NOT_FOUND.getCode()));
            System.out.println("exception: DATA_NOT_FOUND");
            exceptionCount++;
        }
        try {
            // invalid roomId
            moneySprinklingService.receiveMoney(user2, room2, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.DATA_NOT_FOUND.getCode()));
            System.out.println("exception: DATA_NOT_FOUND");
            exceptionCount++;
        }

        // 뿌린 유저가 받으려는 경우
        try {
            moneySprinklingService.receiveMoney(user1, room1, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.INVALID_USER.getCode()));
            System.out.println("exception: INVALID_USER");
            exceptionCount++;
        }

        // 이미 한 번 받은 유저
        moneySprinklingService.receiveMoney(user2, room1, token);
        try {
            moneySprinklingService.receiveMoney(user2, room1, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.INVALID_USER.getCode()));
            System.out.println("exception: INVALID_USER");
            exceptionCount++;
        }

        moneySprinklingService.receiveMoney(user3, room1, token);

        // 모두 받기 완료된 경우
        try {
            moneySprinklingService.receiveMoney(user4, room1, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.MONEY_RUN_OUT.getCode()));
            System.out.println("exception: MONEY_RUN_OUT");
            exceptionCount++;
        }

        // 토큰 만료
        SprinklingStatementEntity entity = sprinklingStatementRepository.findByRoomIdAndToken(room1, token);
        Date newExpiredDate = Date.from(LocalDateTime.now().minusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        entity.setExpiredDate(newExpiredDate);
        sprinklingStatementRepository.save(entity);
        try {
            moneySprinklingService.receiveMoney(user4, room1, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.EXPIRED_TOKEN.getCode()));
            System.out.println("exception: EXPIRED_TOKEN");
            exceptionCount++;
        }

        assertThat(exceptionCount, is(6));
    }

    @Test
    public void getSprinklingInfo_exception_test() {
        int exceptionCount = 0;

        // user1 이 room1 에 2명에게 10,000원 뿌리기
        String token = moneySprinklingService.addMoneySprinkling(user1, room1, amount, distributedNumber);
        String invalidToken = "ABCDEFG";

        // 데이터 없음
        try {
            // invalid token
            moneySprinklingService.getSprinklingInfo(user1, room1, invalidToken);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.DATA_NOT_FOUND.getCode()));
            System.out.println("exception: DATA_NOT_FOUND");
            exceptionCount++;
        }
        try {
            // invalid roomId
            moneySprinklingService.receiveMoney(user1, room2, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.DATA_NOT_FOUND.getCode()));
            System.out.println("exception: DATA_NOT_FOUND");
            exceptionCount++;
        }

        // 뿌린 유저가 아님
        try {
            moneySprinklingService.getSprinklingInfo(user2, room1, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.INVALID_USER.getCode()));
            System.out.println("exception: INVALID_USER");
            exceptionCount++;
        }

        // 조회 기간 만료
        SprinklingStatementEntity entity = sprinklingStatementRepository.findByRoomIdAndToken(room1, token);
        Date newRegDate = Date.from(LocalDateTime.now().minusDays(10).atZone(ZoneId.systemDefault()).toInstant());
        entity.setRegDate(newRegDate);
        sprinklingStatementRepository.save(entity);
        try {
            moneySprinklingService.getSprinklingInfo(user1, room1, token);
        } catch (SprinklingException e) {
            assertThat(e.getResultCode(), is(ResultCodes.SEARCH_PERIOD_EXPIRATION.getCode()));
            System.out.println("exception: SEARCH_PERIOD_EXPIRATION");
            exceptionCount++;
        }

        assertThat(exceptionCount, is(4));
    }
}
