package cinema.controller;

import cinema.dto.BookingRequest;
import cinema.entity.Booking;
import cinema.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Берем имя пользователя из контекста безопасности
            String username = userDetails.getUsername();
            Booking booking = bookingService.createBooking(request, username);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при бронировании: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getUserBookings(userDetails.getUsername()));
    }
}