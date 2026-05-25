package cinema.service;

import cinema.dto.BookingRequest;
import cinema.entity.*;
import cinema.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Session testSession;
    private Seat testSeat;
    private CinemaHall testHall;
    private BookingRequest testRequest;

    @BeforeEach
    void setUp() {
        // Создаем базовые объекты для каждого теста
        testUser = new User();
        testUser.setUsername("vlad_vlad");

        testHall = new CinemaHall();
        testHall.setId(1L);

        testSeat = new Seat();
        testSeat.setId(10L);
        testSeat.setCinemaHall(testHall);

        testSession = new Session();
        testSession.setId(100L);
        testSession.setCinemaHall(testHall);

        testRequest = new BookingRequest();
        testRequest.setSessionId(100L);
        testRequest.setSeatId(10L);
    }

    @Test
    void createBooking_Success() {
        // Настраиваем поведение моков для успешного сценария
        when(userRepository.findByUsername("vlad_vlad")).thenReturn(Optional.of(testUser));
        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        when(seatRepository.findById(10L)).thenReturn(Optional.of(testSeat));
        // Имитируем, что на этот сеанс нет занятых мест
        when(bookingRepository.findReservedSeatIdsBySessionId(100L)).thenReturn(new ArrayList<>());

        // Настраиваем возврат сохраненной брони
        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setUser(testUser);
        savedBooking.setStatus(Booking.BookingStatus.CONFIRMED);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Выполняем метод
        Booking result = bookingService.createBooking(testRequest, "vlad_vlad");

        // Проверяем результаты
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Booking.BookingStatus.CONFIRMED, result.getStatus());

        // Проверяем, что метод save действительно вызвался один раз
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_ThrowsException_IfSeatAlreadyReserved() {
        // Настраиваем поведение моков
        when(userRepository.findByUsername("vlad_vlad")).thenReturn(Optional.of(testUser));
        when(sessionRepository.findById(100L)).thenReturn(Optional.of(testSession));
        when(seatRepository.findById(10L)).thenReturn(Optional.of(testSeat));

        // Имитируем, что ID нашего места (10L) уже находится в списке занятых
        when(bookingRepository.findReservedSeatIdsBySessionId(100L)).thenReturn(List.of(10L));

        // Проверяем, что метод выбросит IllegalStateException с нужным текстом
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(testRequest, "vlad_vlad");
        });

        assertEquals("Это место уже забронировано другим пользователем", exception.getMessage());

        // Метод save не должен вызываться, если место занято
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}