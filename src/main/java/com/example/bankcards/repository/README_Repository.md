```markdown
# Репозитории

Интерфейсы Spring Data JPA для доступа к базе данных.

## 📁 Структура репозиториев

```
src/main/java/com/example/bankcards/repository/
├── UserRepository.java          # Репозиторий для работы с пользователями
└── CardRepository.java          # Репозиторий для работы с картами
```

## 👤 UserRepository

**Сущность:** User

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

### Методы репозитория

#### Стандартные методы Spring Data JPA
- `findAll()` - получение всех пользователей
- `findById(Long id)` - поиск пользователя по ID
- `save(User user)` - сохранение пользователя
- `delete(User user)` - удаление пользователя

#### Кастомные методы
- `findByUsername(String username)` - поиск пользователя по имени
- `existsByUsername(String username)` - проверка существования пользователя

## 💳 CardRepository

**Сущность:** Card

```java
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);
    Optional<Card> findById(Long id);
    Page<Card> findByUser(User user, Pageable pageable);
    
    @Query("SELECT c FROM Card c WHERE c.user = :user " +
           "AND (:ownerName IS NULL OR LOWER(c.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%'))) " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Card> findByUserWithFilters(
            @Param("user") User user,
            @Param("ownerName") String ownerName,
            @Param("status") Status status,
            Pageable pageable
    );
    
    @Query("SELECT c FROM Card c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Card> findByIdWithUser(@Param("id") Long id);
    
    List<Card> findByExpiryDateBeforeAndStatusNot(LocalDate date, Status status);
}
```

### Методы репозитория

#### Стандартные методы Spring Data JPA
- `findAll()` - получение всех карт
- `findById(Long id)` - поиск карты по ID
- `save(Card card)` - сохранение карты
- `delete(Card card)` - удаление карты

#### Кастомные методы запросов
- `findByUser(User user)` - поиск карт пользователя
- `findByUser(User user, Pageable pageable)` - пагинация карт пользователя

#### JPQL запросы
- `findByUserWithFilters()` - фильтрация карт пользователя по имени и статусу
- `findByIdWithUser()` - поиск карты с жадной загрузкой пользователя
- `findByExpiryDateBeforeAndStatusNot()` - поиск просроченных карт

## 🔧 Конфигурация

### Spring Data JPA
- Автоматическая реализация репозиториев
- Поддержка пагинации и сортировки
- Транзакционность на уровне сервиса

### Настройки базы данных
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 📊 Оптимизация запросов

### Индексы
- `users(username)` - для быстрого поиска по имени пользователя
- `cards(user_id)` - для JOIN запросов пользователь-карты
- `cards(status)` - для фильтрации по статусу

### Стратегии загрузки
- **Eager:** Пользователь при загрузке карты (для частого использования)
- **Lazy:** Карты при загрузке пользователя (для оптимизации)

## 🛡️ Безопасность

### Контроль доступа
- Все операции через сервисный слой
- Проверка прав доступа в бизнес-логике
- Валидация входных параметров

### Защита от SQL-инъекций
- Использование параметризованных запросов
- Spring Data JPA экранирование параметров
- JPQL вместо нативных запросов где возможно

## 📋 Best Practices

1. **Интерфейсный подход** - разделение контракта и реализации
2. **Именование методов** - соглашения Spring Data JPA
3. **Пагинация** - для больших наборов данных
4. **Транзакционность** - на уровне сервиса, а не репозитория
5. **Оптимизация запросов** - только необходимые данные
6. **Кэширование** - для часто запрашиваемых данных