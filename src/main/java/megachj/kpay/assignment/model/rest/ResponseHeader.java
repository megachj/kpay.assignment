package megachj.kpay.assignment.model.rest;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHeader {

    private int resultCode;

    private String resultMessage;

    @Getter(AccessLevel.NONE)
    private boolean isSuccessful;

    public boolean getIsSuccessful() {
        return isSuccessful;
    }
}
