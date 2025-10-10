```markdown
# Тесты контроллеров

Unit тесты для REST API с использованием MockMvc.

## 📁 Структура тестов

```
src/test/java/com/example/bankcards/controller/
├── CardControllerTest.java          # Тесты для контроллера карт
└── UserControllerTest.java          # Тесты для контроллера пользователей
```

## 💳 CardControllerTest

**Назначение:** Unit тесты для CardController с проверкой всех эндпоинтов

### Методы тестирования

#### createCard_ShouldReturnCreated()
**Проверка:** Создание новой карты возвращает статус 201 Created

```java
@Test
void createCard_ShouldReturnCreated() {
    // Проверка успешного создания карты
    // Валидация ответа: статус 201, маскированный номер карты
}
```

#### blockCard_ShouldReturnOk()
**Проверка:** Блокировка карты администратором возвращает статус 200 OK

```java
@Test
void blockCard_ShouldReturnOk() {
    // Проверка изменения статуса карты на BLOCKED
}
```

#### activateCard_ShouldReturnOk()
**Проверка:** Активация карты администратором возвращает статус 200 OK

```java
@Test
void activateCard_ShouldReturnOk() {
    // Проверка изменения статуса карты на ACTIVE
}
```

#### blockOwnCard_ShouldReturnOk()
**Проверка:** Блокировка собственной карты пользователем возвращает статус 200 OK

```java
@Test
void blockOwnCard_ShouldReturnOk() {
    // Проверка блокировки карты владельцем
}
```

#### transferMoney_ShouldReturnOk()
**Проверка:** Перевод между картами возвращает статус 200 OK

```java
@Test
void transferMoney_ShouldReturnOk() {
    // Проверка успешного выполнения перевода
}
```

#### getAllCards_ShouldReturnPage()
**Проверка:** Получение всех карт возвращает пагинированный результат

```java
@Test
void getAllCards_ShouldReturnPage() {
    // Проверка пагинации и фильтрации
}
```

#### getBalance_ShouldReturnBalance()
**Проверка:** Получение баланса карты возвращает корректную сумму

```java
@Test
void getBalance_ShouldReturnBalance() {
    // Проверка возврата баланса карты
}
```

#### getMyCards_ShouldReturnPage()
**Проверка:** Получение собственных карт возвращает пагинированный результат

```java
@Test
void getMyCards_ShouldReturnPage() {
    // Проверка пагинации для пользовательских карт
}
```

#### deleteCard_ShouldReturnNoContent()
**Проверка:** Удаление карты возвращает статус 204 No Content

```java
@Test
void deleteCard_ShouldReturnNoContent() {
    // Проверка успешного удаления карты
}
```

## 👤 UserControllerTest

**Назначение:** Unit тесты для UserController с проверкой управления пользователями

### Методы тестирования

#### createUser_ShouldReturnCreated()
**Проверка:** Создание пользователя возвращает статус 201 Created

```java
@Test
void createUser_ShouldReturnCreated() {
    // Проверка успешного создания пользователя
    // Валидация ответа: статус 201, данные пользователя
}
```

#### getAllUsers_ShouldReturnPage()
**Проверка:** Получение всех пользователей возвращает пагинированный результат

```java
@Test
void getAllUsers_ShouldReturnPage() {
    // Проверка пагинации списка пользователей
}
```

#### getUser_ShouldReturnUser()
**Проверка:** Получение пользователя по ID возвращает корректные данные

```java
@Test
void getUser_ShouldReturnUser() {
    // Проверка поиска пользователя по ID
}
```

#### getCurrentUser_ShouldReturnCurrentUser()
**Проверка:** Получение текущего пользователя возвращает корректные данные

```java
@Test
void getCurrentUser_ShouldReturnCurrentUser() {
    // Проверка получения данных аутентифицированного пользователя
}
```

#### updateUserRole_ShouldReturnUpdatedUser()
**Проверка:** Изменение роли пользователя возвращает обновленные данные

```java
@Test
void updateUserRole_ShouldReturnUpdatedUser() {
    // Проверка изменения роли пользователя
}
```

#### updateUsername_ShouldReturnUpdatedUser()
**Проверка:** Изменение имени пользователя возвращает обновленные данные

```java
@Test
void updateUsername_ShouldReturnUpdatedUser() {
    // Проверка изменения username
}
```

#### deleteUser_ShouldReturnNoContent()
**Проверка:** Удаление пользователя возвращает статус 204 No Content

```java
@Test
void deleteUser_ShouldReturnNoContent() {
    // Проверка успешного удаления пользователя
}
```

#### getAllUsersWithoutPagination_ShouldReturnList()
**Проверка:** Получение всех пользователей без пагинации возвращает список

```java
@Test
void getAllUsersWithoutPagination_ShouldReturnList() {
    // Проверка устаревшего метода получения всех пользователей
}
```

## 🧪 Технологии тестирования

### Mockito
- `@Mock` - создание mock объектов
- `@InjectMocks` - внедрение mock в тестируемый класс
- `when().thenReturn()` - настройка поведения mock
- `verify()` - проверка вызовов методов

### JUnit 5
- `@Test` - маркировка тестовых методов
- `@ExtendWith(MockitoExtension.class)` - интеграция с Mockito
- Assertions для проверки результатов

### Структура тестов
- **Given** - подготовка тестовых данных
- **When** - выполнение тестируемого метода
- **Then** - проверка результатов

## 📊 Покрытие тестами

### CardController
- Создание и управление картами
- Финансовые операции (переводы)
- Получение данных с пагинацией и фильтрацией
- Управление статусами карт

### UserController
- CRUD операции для пользователей
- Управление ролями и правами доступа
- Операции с текущим пользователем
- Пагинация и получение данных

## 🔧 Best Practices

1. **Изоляция тестов** - каждый тест независим
2. **Четкие названия** - отражают проверяемую функциональность
3. **Минимальные моки** - только необходимые зависимости
4. **Проверка состояний** и взаимодействий
5. **Coverage** - покрытие основных сценариев использования
