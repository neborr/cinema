package cinema.dto;

import lombok.Data;

@Data
public class CinemaHallDTO {
    private String name;
    private Integer rows;
    private Integer seatsPerRow;
}