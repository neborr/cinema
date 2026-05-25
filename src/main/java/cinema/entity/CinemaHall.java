package cinema.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cinema_halls")
@Data
public class CinemaHall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
}