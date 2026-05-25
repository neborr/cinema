package cinema.service;

import cinema.dto.BookingRequest;
import cinema.entity.*;
import cinema.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Подключаем логер
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // Добавляем логер одной аннотацией
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createBooking(BookingRequest request, String username) {
        log.info("Попытка бронирования: пользователь={}, сеанс={}, место={}", username, request.getSessionId(), request.getSeatId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Ошибка бронирования: пользователь {} не найден", username);
                    return new RuntimeException("Пользователь не найден");
                });

        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Сеанс не найден"));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new RuntimeException("Место не найдено"));

        if (!seat.getCinemaHall().getId().equals(session.getCinemaHall().getId())) {
            log.warn("Ошибка бронирования: место {} не принадлежит залу сеанса {}", request.getSeatId(), session.getCinemaHall().getId());
            throw new IllegalArgumentException("Выбранное место не принадлежит залу этого сеанса");
        }

        List<Long> reservedSeatIds = bookingRepository.findReservedSeatIdsBySessionId(session.getId());
        if (reservedSeatIds.contains(seat.getId())) {
            log.warn("Ошибка бронирования: место {} на сеанс {} уже занято", request.getSeatId(), session.getId());
            throw new IllegalStateException("Это место уже забронировано другим пользователем");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSession(session);
        booking.setSeat(seat);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Успешное бронирование: ID брони={}, пользователь={}", savedBooking.getId(), username);

        return savedBooking;
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