package cinema.dto;

import lombok.Data;

@Data
public class MovieDTO {
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private String ageRating;
}