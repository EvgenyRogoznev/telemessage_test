package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
    String mdn;
    String transactionId;
    String type;
    String requestReceivedDate;
    Integer currentStatus;
    String statusInfo;
}
