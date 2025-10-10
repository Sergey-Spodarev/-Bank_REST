```markdown
# Конфигурация

Содержит классы конфигурации Spring Boot: JWT, безопасность, Swagger, CORS и т.п.

## 📁 Структура конфигурации

```
src/main/java/com/example/bankcards/config/
└── SecurityConfig.java          # Конфигурация безопасности Spring Security
```

## 🛡️ SecurityConfig

**Назначение:** Основная конфигурация безопасности приложения

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
```

### SecurityFilterChain
**Назначение:** Конфигурация цепочки фильтров безопасности

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService);

    return http.build();
}
```

**Настройки:**
- `csrf().disable()` - отключение CSRF защиты для REST API
- `SessionCreationPolicy.STATELESS` - безсессионная аутентификация (JWT)
- Публичные эндпоинты: аутентификация и документация
- Все остальные запросы требуют аутентификации

### AuthenticationManager
**Назначение:** Менеджер аутентификации Spring Security

```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

### PasswordEncoder
**Назначение:** Шифровальщик паролей

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## 🔧 Аннотации конфигурации

- `@Configuration` - класс конфигурации Spring
- `@EnableWebSecurity` - включение безопасности Spring Security
- `@EnableMethodSecurity(prePostEnabled = true)` - включение аннотаций безопасности методов (`@PreAuthorize`, `@PostAuthorize`)

## 📊 Настройки доступа

### Публичные эндпоинты
- `/api/v1/auth/**` - эндпоинты аутентификации
- `/swagger-ui/**` - Swagger UI документация
- `/v3/api-docs/**` - OpenAPI спецификация

### Защищенные эндпоинты
- Все остальные запросы требуют JWT токен
- Проверка прав доступа через аннотации `@PreAuthorize`

## 🎯 Особенности конфигурации

- **Stateless** - не использует сессии, работает с JWT токенами
- **Method Security** - безопасность на уровне методов с аннотациями
- **BCrypt** - надежное шифрование паролей
- **REST API Focused** - оптимизирована для RESTful API