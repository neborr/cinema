package cinema.controller;

import cinema.dto.BookingRequest;
import cinema.entity.Booking;
import cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookTicket(
            @RequestBody BookingRequest request,
            @AuthenticationPrincipal String username) {
        try {
            Booking booking = bookingService.createBooking(request, username);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при бронировании: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(bookingService.getUserBookings(username));
    }
}