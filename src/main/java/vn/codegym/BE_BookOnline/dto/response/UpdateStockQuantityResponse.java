package vn.codegym.BE_BookOnline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStockQuantityResponse {
    private Long bookId;
    private Integer newStockQuantity;
    private String updatedBy;
    private LocalDateTime updatedDate;
}
