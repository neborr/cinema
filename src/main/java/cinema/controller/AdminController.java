package cinema.controller;

import cinema.dto.MovieDTO;
import cinema.dto.SessionDTO;
import cinema.entity.Booking;
import cinema.entity.Movie;
import cinema.entity.Session;
import cinema.service.BookingService;
import cinema.service.MovieService;
import cinema.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MovieService movieService;
    private final SessionService sessionService;
    private final BookingService bookingService;

    @PostMapping("/movies")
    public ResponseEntity<Movie> addMovie(@RequestBody MovieDTO movieDTO) {
        return ResponseEntity.ok(movieService.createMovie(movieDTO));
    }

    @PostMapping("/sessions")
    public ResponseEntity<Session> addSession(@RequestBody SessionDTO sessionDTO) {
        return ResponseEntity.ok(sessionService.createSession(sessionDTO));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookingsForAdmin());
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Фильм успешно удален");
    }
}