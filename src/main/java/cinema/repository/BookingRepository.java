package cinema.repository;

import cinema.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    // Получаем список ID мест, которые уже заняты на конкретном сеансе
    @Query("SELECT b.seat.id FROM Booking b WHERE b.session.id = :sessionId AND b.status = 'CONFIRMED'")
    List<Long> findReservedSeatIdsBySessionId(@Param("sessionId") Long sessionId);
}