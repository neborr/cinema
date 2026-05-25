package cinema.controller;

import cinema.entity.Session;
import cinema.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Session>> getSessionsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(sessionService.getSessionsByMovie(movieId));
    }
}