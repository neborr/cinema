package cinema.service;

import cinema.dto.SessionDTO;
import cinema.entity.CinemaHall;
import cinema.entity.Movie;
import cinema.entity.Session;
import cinema.repository.CinemaHallRepository;
import cinema.repository.MovieRepository;
import cinema.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;
    private final CinemaHallRepository cinemaHallRepository;

    public List<Session> getSessionsByMovie(Long movieId) {
        return sessionRepository.findByMovieId(movieId);
    }

    public Session createSession(SessionDTO dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Фильм не найден"));
        CinemaHall hall = cinemaHallRepository.findById(dto.getHallId())
                .orElseThrow(() -> new RuntimeException("Зал не найден"));

        Session session = new Session();
        session.setMovie(movie);
        session.setCinemaHall(hall);
        session.setStartTime(dto.getStartTime());
        session.setPrice(dto.getPrice());

        return sessionRepository.save(session);
    }
}