```markdown
# Тесты сервисов

Юнит-тесты бизнес-логики с использованием моков.

## 📁 Структура тестов

```
src/test/java/com/example/bankcards/service/
├── CardServiceTest.java          # Тесты для сервиса карт
└── UserServiceTest.java          # Тесты для сервиса пользователей
```

## 💳 CardServiceTest

**Назначение:** Unit тесты для бизнес-логики управления картами и переводами

### Методы тестирования

#### createCard_ShouldCreateCardSuccessfully()
**Проверка:** Успешное создание карты с шифрованием номера

```java
@Test
void createCard_ShouldCreateCardSuccessfully() {
    // Проверка создания карты, шифрования номера и установки статуса ACTIVE
}
```

#### createCard_WithExpiredDate_ShouldThrowException()
**Проверка:** Создание карты с прошедшей датой истечения вызывает исключение

```java
@Test
void createCard_WithExpiredDate_ShouldThrowException() {
    // Проверка валидации даты истечения
}
```

#### createCard_WithNonExistentUser_ShouldThrowException()
**Проверка:** Создание карты для несуществующего пользователя вызывает исключение

```java
@Test
void createCard_WithNonExistentUser_ShouldThrowException() {
    // Проверка существования пользователя
}
```

#### blockCard_ShouldBlockCardSuccessfully()
**Проверка:** Успешная блокировка карты администратором

```java
@Test
void blockCard_ShouldBlockCardSuccessfully() {
    // Проверка изменения статуса на BLOCKED
}
```

#### activateCard_ShouldActivateCardSuccessfully()
**Проверка:** Успешная активация заблокированной карты

```java
@Test
void activateCard_ShouldActivateCardSuccessfully() {
    // Проверка изменения статуса на ACTIVE
}
```

#### activateCard_WithExpiredCard_ShouldThrowException()
**Проверка:** Активация просроченной карты вызывает исключение

```java
@Test
void activateCard_WithExpiredCard_ShouldThrowException() {
    // Проверка невозможности активации просроченной карты
}
```

#### transferMoney_ShouldTransferSuccessfully()
**Проверка:** Успешный перевод между картами одного пользователя

```java
@Test
void transferMoney_ShouldTransferSuccessfully() {
    // Проверка списания и зачисления средств
}
```

#### transferMoney_WithSameCard_ShouldThrowException()
**Проверка:** Перевод на ту же карту вызывает исключение

```java
@Test
void transferMoney_WithSameCard_ShouldThrowException() {
    // Проверка валидации разных карт
}
```

#### transferMoney_WithInsufficientFunds_ShouldThrowException()
**Проверка:** Перевод при недостаточном балансе вызывает исключение

```java
@Test
void transferMoney_WithInsufficientFunds_ShouldThrowException() {
    // Проверка достаточности средств
}
```

#### transferMoney_WithDifferentUsers_ShouldThrowException()
**Проверка:** Перевод между картами разных пользователей вызывает исключение

```java
@Test
void transferMoney_WithDifferentUsers_ShouldThrowException() {
    // Проверка прав доступа к картам
}
```

#### getBalance_ShouldReturnBalance()
**Проверка:** Успешное получение баланса карты

```java
@Test
void getBalance_ShouldReturnBalance() {
    // Проверка возврата корректного баланса
}
```

#### getBalance_WithDifferentUser_ShouldThrowException()
**Проверка:** Получение баланса чужой карты вызывает исключение

```java
@Test
void getBalance_WithDifferentUser_ShouldThrowException() {
    // Проверка прав доступа к балансу
}
```

#### blockOwnCard_ShouldBlockOwnCardSuccessfully()
**Проверка:** Успешная блокировка собственной карты пользователем

```java
@Test
void blockOwnCard_ShouldBlockOwnCardSuccessfully() {
    // Проверка блокировки карты владельцем
}
```

#### blockOwnCard_WithDifferentUser_ShouldThrowException()
**Проверка:** Блокировка чужой карты вызывает исключение

```java
@Test
void blockOwnCard_WithDifferentUser_ShouldThrowException() {
    // Проверка прав доступа к блокировке
}
```

#### getMyCardsWithFilter_ShouldReturnFilteredCards()
**Проверка:** Успешное получение отфильтрованных карт пользователя

```java
@Test
void getMyCardsWithFilter_ShouldReturnFilteredCards() {
    // Проверка фильтрации и пагинации карт
}
```

#### deleteCard_ShouldDeleteCardSuccessfully()
**Проверка:** Успешное удаление карты

```java
@Test
void deleteCard_ShouldDeleteCardSuccessfully() {
    // Проверка удаления карты из репозитория
}
```

#### getCardById_WithNonExistentCard_ShouldThrowException()
**Проверка:** Поиск несуществующей карты вызывает исключение

```java
@Test
void getCardById_WithNonExistentCard_ShouldThrowException() {
    // Проверка обработки отсутствующей карты
}
```

## 👤 UserServiceTest

**Назначение:** Unit тесты для бизнес-логики управления пользователями

### Методы тестирования

#### createUser_ShouldCreateUserSuccessfully()
**Проверка:** Успешное создание пользователя с шифрованием пароля

```java
@Test
void createUser_ShouldCreateUserSuccessfully() {
    // Проверка создания пользователя с шифрованием пароля
}
```

#### createUser_WithExistingUsername_ShouldThrowException()
**Проверка:** Создание пользователя с существующим username вызывает исключение

```java
@Test
void createUser_WithExistingUsername_ShouldThrowException() {
    // Проверка уникальности username
}
```

#### createUser_WithNullRole_ShouldUseDefaultRole()
**Проверка:** Создание пользователя без роли использует роль по умолчанию

```java
@Test
void createUser_WithNullRole_ShouldUseDefaultRole() {
    // Проверка установки роли по умолчанию
}
```

#### findAllUsers_ShouldReturnPage()
**Проверка:** Успешное получение страницы пользователей

```java
@Test
void findAllUsers_ShouldReturnPage() {
    // Проверка пагинации пользователей
}
```

#### findAllUsersList_ShouldReturnList()
**Проверка:** Успешное получение списка пользователей

```java
@Test
void findAllUsersList_ShouldReturnList() {
    // Проверка получения всех пользователей
}
```

#### findUserByUserId_ShouldReturnUser()
**Проверка:** Успешный поиск пользователя по ID

```java
@Test
void findUserByUserId_ShouldReturnUser() {
    // Проверка поиска пользователя по идентификатору
}
```

#### findUserByUserId_WithNonExistentUser_ShouldThrowException()
**Проверка:** Поиск несуществующего пользователя вызывает исключение

```java
@Test
void findUserByUserId_WithNonExistentUser_ShouldThrowException() {
    // Проверка обработки отсутствующего пользователя
}
```

#### getCurrentUser_ShouldReturnCurrentUser()
**Проверка:** Успешное получение текущего пользователя

```java
@Test
void getCurrentUser_ShouldReturnCurrentUser() {
    // Проверка получения данных аутентифицированного пользователя
}
```

#### getCurrentUser_WithNonExistentUser_ShouldThrowException()
**Проверка:** Получение несуществующего текущего пользователя вызывает исключение

```java
@Test
void getCurrentUser_WithNonExistentUser_ShouldThrowException() {
    // Проверка обработки отсутствующего текущего пользователя
}
```

#### updateUserRole_ShouldUpdateRoleSuccessfully()
**Проверка:** Успешное обновление роли пользователя

```java
@Test
void updateUserRole_ShouldUpdateRoleSuccessfully() {
    // Проверка изменения роли пользователя
}
```

#### updateUserRole_WithSameUser_ShouldThrowException()
**Проверка:** Изменение собственной роли вызывает исключение

```java
@Test
void updateUserRole_WithSameUser_ShouldThrowException() {
    // Проверка запрета изменения собственной роли
}
```

#### updateUsername_ShouldUpdateUsernameSuccessfully()
**Проверка:** Успешное обновление имени пользователя

```java
@Test
void updateUsername_ShouldUpdateUsernameSuccessfully() {
    // Проверка изменения username
}
```

#### updateUsername_WithSameUsername_ShouldThrowException()
**Проверка:** Изменение на тот же username вызывает исключение

```java
@Test
void updateUsername_WithSameUsername_ShouldThrowException() {
    // Проверка изменения на текущий username
}
```

#### updateUsername_WithExistingUsername_ShouldThrowException()
**Проверка:** Изменение на существующий username вызывает исключение

```java
@Test
void updateUsername_WithExistingUsername_ShouldThrowException() {
    // Проверка уникальности нового username
}
```

#### deleteUser_ShouldDeleteUserSuccessfully()
**Проверка:** Успешное удаление пользователя

```java
@Test
void deleteUser_ShouldDeleteUserSuccessfully() {
    // Проверка удаления пользователя
}
```

#### deleteUser_WithSameUser_ShouldThrowException()
**Проверка:** Удаление самого себя вызывает исключение

```java
@Test
void deleteUser_WithSameUser_ShouldThrowException() {
    // Проверка запрета удаления собственного аккаунта
}
```

#### swapRoleToAdmin_ShouldCallUpdateUserRole()
**Проверка:** Метод swapRoleToAdmin вызывает updateUserRole

```java
@Test
void swapRoleToAdmin_ShouldCallUpdateUserRole() {
    // Проверка вызова метода обновления роли
}
```

#### swapUserName_ShouldCallUpdateUsername()
**Проверка:** Метод swapUserName вызывает updateUsername

```java
@Test
void swapUserName_ShouldCallUpdateUsername() {
    // Проверка вызова метода обновления username
}
```

#### deleteUser_WithoutAuth_ShouldCallDeleteUserWithAuth()
**Проверка:** Метод deleteUser без аутентификации вызывает deleteUser с аутентификацией

```java
@Test
void deleteUser_WithoutAuth_ShouldCallDeleteUserWithAuth() {
    // Проверка вызова метода удаления с аутентификацией
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

### CardService
- Создание и валидация карт
- Управление статусами карт
- Финансовые операции и переводы
- Проверка прав доступа
- Получение и фильтрация данных

### UserService
- CRUD операции для пользователей
- Управление ролями и правами доступа
- Обновление пользовательских данных
- Операции с текущим пользователем
- Безопасное удаление пользователей

## 🔧 Best Practices

1. **Изоляция тестов** - каждый тест независим
2. **Четкие названия** - отражают проверяемую функциональность
3. **Минимальные моки** - только необходимые зависимости
4. **Проверка состояний** и взаимодействий
5. **Coverage** - покрытие основных сценариев использования
```