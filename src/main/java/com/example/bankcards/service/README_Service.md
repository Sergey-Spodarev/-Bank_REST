```markdown
# Сервисы

Бизнес-логика: управление пользователями, картами и переводами.

## 📁 Структура сервисов

```
src/main/java/com/example/bankcards/service/
├── UserService.java             # Бизнес-логика пользователей
├── CardService.java             # Бизнес-логика карт и переводов
└── AuthService.java             # Сервис аутентификации
```

## 👤 UserService

**Назначение:** Управление пользователями и их данными

```java
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Основные методы бизнес-логики
}
```

### Основные методы

#### Создание и управление
- `createUser(LoginUserDTO user)` - создание нового пользователя с шифрованием пароля
- `findAllUsers(Pageable pageable)` - получение всех пользователей с пагинацией
- `findUserByUserId(Long userId)` - поиск пользователя по ID

#### Обновление данных
- `updateUserRole(Long userId, Role newRole, Authentication auth)` - изменение роли пользователя с проверкой безопасности
- `updateUsername(Authentication auth, String newUsername)` - обновление имени пользователя

#### Безопасность
- `getCurrentUser(Authentication auth)` - получение текущего аутентифицированного пользователя
- `deleteUser(Long userId, Authentication auth)` - удаление пользователя с проверкой прав

### Бизнес-правила
- Нельзя изменить роль самому себе
- Нельзя удалить самого себя
- Username должен быть уникальным
- Валидация длины и формата данных

## 💳 CardService

**Назначение:** Управление банковскими картами и финансовыми операциями

```java
@Service
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberEncryptor encryptor;
    
    // Основные методы бизнес-логики
}
```

### Основные методы

#### Управление картами
- `createCard(Long userId, CreateCardDTO createDTO)` - создание карты с шифрованием номера
- `blockCard(Long cardId)` - блокировка карты администратором
- `activateCard(Long cardId)` - активация карты администратором
- `blockOwnCard(Long cardId)` - блокировка карты владельцем

#### Финансовые операции
- `transferMoney(CardTransferRequestDTO transferDTO)` - перевод между картами с полной валидацией
- `getBalance(Long cardId)` - получение баланса карты

#### Поиск и фильтрация
- `getAllCardsWithFilters(String ownerName, Status status, Pageable pageable)` - все карты с фильтрами (ADMIN)
- `getMyCardsWithFilter(String ownerName, Status status, Pageable pageable)` - карты пользователя с фильтрами

### Бизнес-правила

#### Переводы
- Перевод только между своими картами
- Карты должны быть активными
- Достаточный баланс на исходной карте
- Сумма перевода должна быть положительной
- Нельзя переводить на ту же карту

#### Статусы карт
- Автоматическая проверка срока действия
- Невозможность операций с заблокированными картами
- Невозможность активации просроченных карт

## 🔐 AuthService

**Назначение:** Аутентификация и управление JWT токенами

```java
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    // Методы аутентификации
}
```

### Основные методы
- `authenticate(String username, String password)` - аутентификация пользователя
- `generateToken(User user)` - генерация JWT токена
- `validateToken(String token)` - валидация токена

## 🛡️ Безопасность

### Проверки доступа
- Владелец карты может управлять только своими картами
- Администратор имеет полный доступ
- Проверка активного статуса для операций
- Валидация суммы операций

### Обработка исключений
- `CardNotFoundException` - карта не найдена
- `InsufficientFundsException` - недостаточно средств
- `UserNotFoundException` - пользователь не найден
- `UsernameAlreadyTakenException` - имя пользователя занято

## ⚡ Транзакционность

### Уровень сервиса
- `@Transactional` на уровне класса
- Atomic операции для финансовых транзакций
- Откат при ошибках бизнес-логики

### Особенности переводов
- Переводы выполняются в одной транзакции
- Проверка баланса перед списанием
- Гарантия согласованности данных

## 🔄 Scheduled задачи

### Автоматические операции
```java
@Scheduled(cron = "0 0 0 * * ?")
public void updateExpiredCards() {
    // Ежедневная проверка просроченных карт
}
```

## 📊 Логирование

### Уровни логирования
- `INFO` - успешные операции
- `WARN` - попытки неавторизованного доступа
- `ERROR` - ошибки бизнес-логики

### Аудирование операций
- Создание, блокировка, удаление карт
- Финансовые транзакции
- Изменение ролей пользователей

## 📋 Best Practices

1. **Разделение ответственности** - каждый сервис отвечает за свою область
2. **Транзакционность** - границы транзакций на уровне сервиса
3. **Обработка ошибок** - кастомные исключения для бизнес-ошибок
4. **Валидация** - проверка данных перед операциями
5. **Логирование** - отслеживание важных операций
6. **Производительность** - оптимизация запросов к БД