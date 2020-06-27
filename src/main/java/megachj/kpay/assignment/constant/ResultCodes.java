package megachj.kpay.assignment.constant;

import lombok.Getter;

public enum ResultCodes {
    OK(0, "SUCCESS"),
    ILLEGAL_ARGUMENT(-400_00_00, "Illegal argument."),
    DATA_NOT_FOUND(-400_00_01, "Data not found."),
    EXPIRED_TOKEN(-400_00_02, "Expired token."),
    INVALID_USER(-400_00_03, "Invalid userId."),
    MONEY_RUN_OUT(-400_00_04, "The money has run out."),
    SEARCH_PERIOD_EXPIRATION(-400_00_05, "The search period expiration."),
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
