package cinema.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SessionDTO {
    private Long movieId;
    private Long hallId;
    private LocalDateTime startTime;
    private BigDecimal price;
}