package megachj.kpay.assignment.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SprinklingInfoResponse {

    private ResponseHeader header;

    private SprinklingInfo sprinklingInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SprinklingInfo {
        private LocalDateTime regDateTime;

        private int registrationAmount;

        private int totalReceivedAmount;

        private List<ReceiveInfo> receiveInfoList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiveInfo {

        private int receivedAmount;

        private int userId;
    }
}