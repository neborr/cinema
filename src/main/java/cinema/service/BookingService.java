package cinema.service;

import cinema.dto.BookingRequest;
import cinema.entity.*;
import cinema.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(BookingRequest request, String username) {
        //Проверяем, существует ли пользователь, сеанс и место
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Сеанс не найден"));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new RuntimeException("Место не найдено"));

        // Проверяем, относится ли место к залу этого сеанса
        if (!seat.getCinemaHall().getId().equals(session.getCinemaHall().getId())) {
            throw new IllegalArgumentException("Выбранное место не принадлежит залу этого сеанса");
        }

        // Проверяем, не занято ли уже это место на данный сеанс
        List<Long> reservedSeatIds = bookingRepository.findReservedSeatIdsBySessionId(session.getId());
        if (reservedSeatIds.contains(seat.getId())) {
            throw new IllegalStateException("Это место уже забронировано другим пользователем");
        }

        // Оформляем бронирование
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSession(session);
        booking.setSeat(seat);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return bookingRepository.findByUserId(user.getId());
    }

    public List<Booking> getAllBookingsForAdmin() {
        return bookingRepository.findAll();
    }
}