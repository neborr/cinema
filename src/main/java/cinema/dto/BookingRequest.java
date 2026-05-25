package cinema.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long sessionId;
    private Long seatId;
}