package cinema.controller;

import cinema.dto.AuthRequest;
import cinema.entity.User;
import cinema.config.JwtCore;
import cinema.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtCore jwtCore;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Имя пользователя уже занято!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Хэшируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER); // По умолчанию обычный пользователь

        userRepository.save(user);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Неверное имя пользователя или пароль"));

        // Проверяем совпадает ли введенный пароль с хэшем из БД
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Неверное имя пользователя или пароль");
        }

        // Если всё ок, генерируем JWT токен
        String token = jwtCore.generateToken(user);
        return ResponseEntity.ok(token);
    }
}