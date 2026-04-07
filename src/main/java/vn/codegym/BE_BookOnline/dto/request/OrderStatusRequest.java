package vn.codegym.BE_BookOnline.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.codegym.BE_BookOnline.model.Enum.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusRequest {
   private OrderStatus newStatus;
   @Size(max = 255, message = "Lý do thay đổi trạng thái không được vượt quá 255 ký tự")
   private String reason;
}
