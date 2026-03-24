package vn.codegym.BE_BookOnline.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeDetail {
    private int fee;                        // phí ship (VND)

    @JsonProperty("insurance_fee")
    private int insuranceFee;               // phí bảo hiểm

    @JsonProperty("estimated_pick_time")
    private String estimatedPickTime;       // thời gian lấy hàng

    @JsonProperty("estimated_deliver_time")
    private String estimatedDeliverTime;    // thời gian giao hàng dự kiến

    @JsonProperty("ship_fee_only")
    private int shipFeeOnly;
}
