package vn.codegym.BE_BookOnline.model.Enum;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
    PENDING {
        @Override
        public Set<OrderStatus> allowedNextStatuses() {
            return EnumSet.of(CONFIRMED, CANCELLED);
        }
    },
    CONFIRMED {
        @Override
        public Set<OrderStatus> allowedNextStatuses() {
            return EnumSet.of(PROCESSING, CANCELLED);
        }
    },
    PROCESSING {
        @Override
        public Set<OrderStatus> allowedNextStatuses() {
            return EnumSet.of(SHIPPING, CANCELLED);
        }
    },
    SHIPPING {
        @Override
        public Set<OrderStatus> allowedNextStatuses() {
            return EnumSet.of(COMPLETED);
            // SHIPPING không được hủy, chỉ có thể hoàn thành
        }
    },
    COMPLETED {
        @Override
        public Set<OrderStatus> allowedNextStatuses() {
            return EnumSet.noneOf(OrderStatus.class); // Không đi đâu được nữa
        }
    },
    CANCELLED {
        @Override
        public Set<OrderStatus> allowedNextStatuses() {
            return EnumSet.noneOf(OrderStatus.class); // Không đi đâu được nữa
        }
    };

    public abstract Set<OrderStatus> allowedNextStatuses();

    public boolean canTransitionTo(OrderStatus next) {
        return allowedNextStatuses().contains(next);
    }
}
