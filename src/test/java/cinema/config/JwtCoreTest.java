package cinema.config;

import cinema.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtCoreTest {

    private JwtCore jwtCore;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtCore = new JwtCore();

        testUser = new User();
        testUser.setUsername("test_vlad");
        testUser.setRole(User.Role.USER);
    }

    @Test
    void testGenerateAndValidateToken_Success() {
        // Генерируем токен
        String token = jwtCore.generateToken(testUser);
        assertNotNull(token);

        // Проверяем, что он валидный
        assertTrue(jwtCore.validateToken(token));

        // Проверяем, что из него корректно извлекается имя
        String username = jwtCore.getNameFromToken(token);
        assertEquals("test_vlad", username);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Передаем заведомо сломанную строку вместо токена
        String invalidToken = "not.a.valid.jwt.token";

        assertFalse(jwtCore.validateToken(invalidToken));
    }
}