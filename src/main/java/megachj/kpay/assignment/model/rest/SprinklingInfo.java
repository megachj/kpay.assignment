package megachj.kpay.assignment.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import megachj.kpay.assignment.model.dto.DistributedInfo;
import megachj.kpay.assignment.model.entity.DistributedInfoEntity;
import megachj.kpay.assignment.model.entity.SprinklingStatementEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprinklingInfo {

    private String regDateTime;

    private int registrationAmount;

    private int totalReceivedAmount;

    private List<ReceiveInfo> receiveInfoList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiveInfo {

        private int receivedAmount;

        private int userId;
    }

    public static SprinklingInfo of(SprinklingStatementEntity entity) {
        SprinklingInfo sprinklingInfo = new SprinklingInfo();
        sprinklingInfo.setRegDateTime(LocalDateTime.ofInstant(entity.getRegDate().toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME));
        sprinklingInfo.setRegistrationAmount(entity.getAmount());

        List<ReceiveInfo> receiveInfoList = entity.getDistributedInfoEntityList()
                .stream()
                .filter(v -> v.getState() == DistributedInfo.State.DONE)
                .map(v -> new ReceiveInfo(v.getAmount(), v.getUserId()))
                .collect(Collectors.toList());
        sprinklingInfo.setReceiveInfoList(receiveInfoList);

        int totalReceivedAmount = receiveInfoList.stream().mapToInt(ReceiveInfo::getReceivedAmount).sum();
        sprinklingInfo.setTotalReceivedAmount(totalReceivedAmount);

        return sprinklingInfo;
    }
}
