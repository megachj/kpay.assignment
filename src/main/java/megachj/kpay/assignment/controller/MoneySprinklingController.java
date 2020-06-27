package megachj.kpay.assignment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import megachj.kpay.assignment.constant.ResultCodes;
import megachj.kpay.assignment.model.dto.SprinklingInfo;
import megachj.kpay.assignment.model.rest.ReceiveMoneyResponse;
import megachj.kpay.assignment.model.rest.RestResponseHeader;
import megachj.kpay.assignment.model.rest.SprinklingInfoResponse;
import megachj.kpay.assignment.model.rest.SprinklingRegistrationResponse;
import megachj.kpay.assignment.service.MoneySprinklingService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/money-sprinkling/v1.0")
public class MoneySprinklingController {

    private static final String HTTP_HEADER_USER_ID = "X-USER-ID";

    private static final String HTTP_HEADER_ROOM_ID = "X-ROOM-ID";

    private final MoneySprinklingService moneySprinklingService;

    /**
     * 뿌리기 API
     *
     * @param userId
     * @param roomId
     * @param amount
     * @param distributedNumber
     */
    @PostMapping("/money")
    public SprinklingRegistrationResponse registerMoneySprinkling(@RequestHeader(HTTP_HEADER_USER_ID) int userId,
                                                                  @RequestHeader(HTTP_HEADER_ROOM_ID) String roomId,
                                                                  @RequestParam int amount,
                                                                  @RequestParam int distributedNumber) {
        if (log.isDebugEnabled())
            log.debug("userId: {}, roomId: {}, amount: {}, distributedNumber: {}", userId, roomId, amount, distributedNumber);

        String token = moneySprinklingService.addMoneySprinkling(userId, roomId, amount, distributedNumber);

        return new SprinklingRegistrationResponse(new RestResponseHeader(ResultCodes.OK.getCode(), ResultCodes.OK.getDefaultMessage(), true), token);
    }

    /**
     * 받기 API
     *
     * @param userId
     * @param roomId
     * @param token
     */
    @PutMapping("/money/receive")
    public ReceiveMoneyResponse receiveMoney(@RequestHeader(HTTP_HEADER_USER_ID) int userId,
                                             @RequestHeader(HTTP_HEADER_ROOM_ID) String roomId,
                                             @RequestParam String token) {
        if (log.isDebugEnabled())
            log.debug("userId: {}, roomId: {}, token: {}", userId, roomId, token);

        int receivedAmount = moneySprinklingService.receiveMoney(userId, roomId, token);

        return new ReceiveMoneyResponse(new RestResponseHeader(ResultCodes.OK.getCode(), ResultCodes.OK.getDefaultMessage(), true), receivedAmount);
    }

    /**
     * 조회 API
     *
     * @param userId
     * @param roomId
     * @param token
     */
    @GetMapping("/sprinkling-info")
    public SprinklingInfoResponse getSprinklingInfo(@RequestHeader(HTTP_HEADER_USER_ID) int userId,
                                                    @RequestHeader(HTTP_HEADER_ROOM_ID) String roomId,
                                                    @RequestParam String token) {
        if (log.isDebugEnabled())
            log.debug("userId: {}, roomId: {}, token: {}", userId, roomId, token);

        SprinklingInfo sprinklingInfo = moneySprinklingService.getSprinklingInfo(userId, roomId, token);

        return new SprinklingInfoResponse(new RestResponseHeader(ResultCodes.OK.getCode(), ResultCodes.OK.getDefaultMessage(), true), sprinklingInfo);
    }
}
