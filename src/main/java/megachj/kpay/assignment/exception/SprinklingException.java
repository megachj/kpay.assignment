package megachj.kpay.assignment.exception;

import lombok.Getter;

@Getter
public class SprinklingException extends RuntimeException {

    protected final long trackingTime;

    protected final int resultCode;

    public SprinklingException(long trackingTime, int resultCode, final String message) {
        super(message);
        this.trackingTime = trackingTime;
        this.resultCode = resultCode;
    }
}
