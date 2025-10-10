```markdown
# Сущности

JPA-сущности: Card, User, Role и другие.

## 📁 Структура сущностей

```
src/main/java/com/example/bankcards/entity/
├── User.java                    # Сущность пользователя
├── Card.java                    # Сущность банковской карты
├── Role.java                    # Перечисление ролей
└── Status.java                  # Перечисление статусов карт
```

## 👤 User Entity

**Таблица:** `users`

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
```

**Связи:**
- `OneToMany` с Card (один пользователь - много карт)

**Ограничения:**
- Уникальный username
- Обязательные поля: username, password, role

## 💳 Card Entity

**Таблица:** `cards`

```java
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number_encrypted", nullable = false)
    private String cardNumberEncrypted;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

**Связи:**
- `ManyToOne` с User (много карт - один пользователь)

**Ограничения:**
- Все поля обязательные
- Баланс не может быть null

## 🎭 Role Enum

**Назначение:** Определение ролей пользователей в системе

```java
public enum Role {
    USER,
    ADMIN
}
```

**Использование:**
- Аутентификация и авторизация
- Разграничение прав доступа
- Валидация операций

## 📊 Status Enum

**Назначение:** Определение статусов банковских карт

```java
public enum Status {
    ACTIVE,     // Карта активна и может использоваться
    BLOCKED,    //Карта заблокирована
    EXPIRED     //Срок действия карты истек
}
```

**Бизнес-логика:**
- Только ACTIVE карты могут выполнять операции
- Автоматическая проверка срока действия
- Ручная блокировка пользователем или администратором

## 🔗 Связи между сущностями

### User ↔ Card
```
User (1) ←→ (N) Card
```

**Особенности:**
- Каскадное удаление не используется
- Eager/Lazy loading в зависимости от сценария
- Индексы на часто используемых полях

## 🗃️ Миграции базы данных

**Liquibase миграции:**
- Создание таблиц с ограничениями
- Индексы для оптимизации запросов
- Начальные данные (администратор)

## 🛡️ Безопасность данных

### Шифрование:
- Номера карт хранятся в зашифрованном виде
- Пароли хэшируются с использованием bcrypt
- Чувствительные данные никогда не логируются

### Валидация:
- Ограничения на уровне базы данных
- Валидация на уровне приложения
- Проверка бизнес-правил

## 📋 Best Practices

1. **Соглашения об именовании** - единый стиль для таблиц и колонок
2. **Индексы** - оптимизация частых запросов
3. **Валидация** - проверка на нескольких уровнях
4. **Аудирование** - отслеживание изменений (если нужно)
5. **Оптимизация** - правильные стратегии загрузки данных
