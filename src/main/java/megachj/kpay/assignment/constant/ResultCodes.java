package megachj.kpay.assignment.constant;

import lombok.Getter;

public enum ResultCodes {
    // 성공
    OK(0, "SUCCESS"),
    // 유효하지 않은 파라미터
    ILLEGAL_ARGUMENT(-400_00_00, "Illegal argument."),
    // 데이터를 찾을 수 없음
    DATA_NOT_FOUND(-400_00_01, "Data not found."),
    // 토큰 만료 예외
    EXPIRED_TOKEN(-400_00_02, "Expired token."),
    // API 로직에 따른 유효하지 않은 userId 예외
    INVALID_USER(-400_00_03, "Invalid userId."),
    // 뿌린 돈이 모두 회수된 상태
    MONEY_RUN_OUT(-400_00_04, "The money has run out."),
    // 조회 기간 만료 예외
    SEARCH_PERIOD_EXPIRATION(-400_00_05, "The search period expiration."),
    // 데이터 정합성 예외
    DATA_INTEGRITY_VIOLATION(-400_00_06, "Data integrity violation. Please Retry."),
    // 여러 유저가 동시에 돈을 가져갈때 발생하는 예외(동기화, 낙관적 락 예외)
    OPTIMISTIC_LOCKING_FAILURE(-400_01_00, "Synchronization exception. Please Retry."),
    // 서버 내부 오류
    INTERNAL_SERVER_ERROR(-500_00_00, "Internal server error.");

    @Getter
    private int code;

    @Getter
    private String defaultMessage;

    ResultCodes(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}
