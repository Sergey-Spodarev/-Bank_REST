```markdown
# Безопасность

Конфигурации и компоненты безопасности: JWT, фильтры, UserDetailsService.

## 📁 Структура безопасности

```
src/main/java/com/example/bankcards/security/
├── CustomUserDetailsService.java    # Сервис для загрузки пользователей
└── CustomUserDetails.java          # Реализация UserDetails
```

## 👤 CustomUserDetailsService

**Назначение:** Сервис для загрузки данных пользователя при аутентификации

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new CustomUserDetails(user, authorities);
    }
}
```

**Функциональность:**
- Поиск пользователя по username в базе данных
- Создание списка прав доступа на основе роли пользователя
- Возврат CustomUserDetails с данными пользователя

## 🔐 CustomUserDetails

**Назначение:** Реализация UserDetails для хранения информации о пользователе

```java
public class CustomUserDetails implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String userName;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean credentialsNonExpired;
    private final boolean accountNonLocked;
    private final User user;
}
```

**Поля:**
- `userName` - имя пользователя
- `password` - зашифрованный пароль
- `authorities` - список прав доступа (роли)
- `user` - объект сущности User

**Методы UserDetails:**
- `getAuthorities()` - возвращает список прав пользователя
- `getPassword()` - возвращает пароль пользователя
- `getUsername()` - возвращает имя пользователя
- `isAccountNonExpired()` - проверяет не истек ли срок учетной записи
- `isAccountNonLocked()` - проверяет не заблокирована ли учетная запись
- `isCredentialsNonExpired()` - проверяет не истекли ли учетные данные
- `isEnabled()` - проверяет активна ли учетная запись

**Дополнительные методы:**
- `getUser()` - возвращает объект User сущности
- `getUserName()` - возвращает имя пользователя
```