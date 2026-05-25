package cinema.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = null;
        String header = request.getHeader("Authorization");

        try {
            // 1. Проверяем наличие заголовка Authorization и префикса Bearer
            if (header != null && header.startsWith("Bearer ")) {
                jwt = header.substring(7);
            }

            // 2. Если токен нашли и он прошел валидацию
            if (jwt != null && jwtCore.validateToken(jwt)) {
                String username = jwtCore.getNameFromToken(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Создаем объект аутентификации для Spring Security
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList() // Здесь можно указать роли/права доступа, если они есть в токене
                    );

                    // Передаем пользователя в контекст Spring - теперь он авторизован
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Пользователь {} успешно авторизован по JWT", username);
                }
            }
        } catch (Exception e) {
            // Если токен невалидный, упал по таймауту или поврежден - пишем в консоль причину
            log.error("Ошибка аутентификации по JWT токену: {}", e.getMessage());
        }

        // Передаем запрос дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
}