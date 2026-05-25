package cinema.controller;

import cinema.dto.MovieDTO;
import cinema.dto.SessionDTO;
import cinema.entity.Movie;
import cinema.entity.Session;
import cinema.service.BookingService;
import cinema.service.MovieService;
import cinema.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Mock
    private MovieService movieService;

    @Mock
    private SessionService sessionService;

    @Mock
    private BookingService bookingService;

    @Mock
    private cinema.config.JwtCore jwtCore;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .addFilters(new org.springframework.web.filter.CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    void addMovie_Success() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Начало");
        movieDTO.setDuration(148);

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("Начало");
        savedMovie.setDuration(148);

        when(movieService.createMovie(any(MovieDTO.class))).thenReturn(savedMovie);

        mockMvc.perform(post("/api/admin/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Начало"));
    }

    @Test
    void addSession_Success() throws Exception {
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setMovieId(1L);
        sessionDTO.setHallId(2L);
        sessionDTO.setStartTime(LocalDateTime.of(2026, 6, 1, 18, 0));
        sessionDTO.setPrice(BigDecimal.valueOf(450));

        Session savedSession = new Session();
        savedSession.setId(10L);
        savedSession.setPrice(BigDecimal.valueOf(450));

        when(sessionService.createSession(any(SessionDTO.class))).thenReturn(savedSession);

        mockMvc.perform(post("/api/admin/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.price").value(450));
    }

    @Test
    void deleteMovie_Success() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/admin/movies/1"))
                .andExpect(status().isOk());

        org.mockito.Mockito.verify(movieService).deleteMovie(1L);
    }
}