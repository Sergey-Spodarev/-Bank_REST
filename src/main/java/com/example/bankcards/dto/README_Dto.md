```markdown
# DTO (Data Transfer Objects)

Объекты передачи данных между слоями приложения.

## 📁 Структура DTO

```
src/main/java/com/example/bankcards/dto/
├── UserDTO.java                 # Данные пользователя для ответа
├── LoginUserDTO.java            # Данные для входа/создания пользователя
├── CardResponseDTO.java         # Данные карты для ответа
├── CreateCardDTO.java           # Данные для создания карты
└── CardTransferRequestDTO.java  # Данные для перевода между картами
```

## 👤 User DTOs

### UserDTO
**Назначение:** Передача данных пользователя в ответах API

```java
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
}
```

**Использование:**
- Ответы на запросы информации о пользователе
- Списки пользователей
- Обновленные данные пользователя

### LoginUserDTO
**Назначение:** Получение данных для входа или создания пользователя

```java
public class LoginUserDTO {
    private String username;
    private String password;
    private Role role;
}
```

**Валидация:**
- `@NotBlank` для username и password
- `@Size(min = 3, max = 50)` для username
- `@Size(min = 6)` для password
- `@NotNull` для role

## 💳 Card DTOs

### CardResponseDTO
**Назначение:** Передача данных карты в ответах API

```java
public class CardResponseDTO {
    private Long id;
    private String maskedCardNumber;  // Формат: **** **** **** 1234
    private String ownerName;
    private LocalDate expiryDate;
    private Status status;
    private BigDecimal balance;
}
```

**Особенности:**
- Номер карты маскируется перед отправкой
- Все поля обязательные для заполнения

### CreateCardDTO
**Назначение:** Получение данных для создания новой карты

```java
public class CreateCardDTO {
    private String cardNumberPlain;  // Открытый номер для шифрования
    private String ownerName;
    private LocalDate expiryDate;
}
```

**Валидация:**
- `@NotBlank` для cardNumberPlain и ownerName
- `@Future` для expiryDate
- `@NotNull` для всех полей

### CardTransferRequestDTO
**Назначение:** Данные для перевода средств между картами

```java
public class CardTransferRequestDTO {
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
}
```

**Валидация:**
- `@NotNull` для всех полей
- `@DecimalMin("0.01")` для amount
- Кастомная проверка: fromCardId != toCardId

## 🔄 Преобразования

### Entity → DTO
- Шифрование/маскирование номеров карт
- Исключение конфиденциальных данных (пароли)
- Форматирование данных для клиента

### DTO → Entity
- Валидация входных данных
- Шифрование конфиденциальной информации
- Установка значений по умолчанию

## 🛡️ Безопасность

### Защита данных:
- Пароли никогда не передаются в DTO ответов
- Номера карт маскируются в ответах
- Конфиденциальные данные шифруются при сохранении

### Валидация:
- Аннотации валидации Spring Boot
- Кастомные валидаторы для бизнес-логики
- Глобальная обработка ошибок валидации

## 📋 Best Practices

1. **Разделение ответственности** - разные DTO для запросов и ответов
2. **Минимализм** - только необходимые поля в каждом DTO
3. **Валидация** - проверка данных на уровне DTO
4. **Неизменяемость** - использование final полей где возможно
5. **Документация** - четкое описание назначения каждого DTO