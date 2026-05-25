package cinema.service;

import cinema.entity.Movie;
import cinema.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void getMovieById_Success() {
        // Настраиваем мок кинотеатра
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Интерстеллар");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(1L);

        // Проверки
        assertNotNull(result);
        assertEquals("Интерстеллар", result.getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieById_ThrowsException_IfNotFound() {
        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            movieService.getMovieById(999L);
        });

        assertEquals("Фильм не найден", exception.getMessage());
    }
}