```markdown
# Обработка исключений

Глобальный обработчик ошибок и пользовательские исключения.

## 📁 Структура исключений

```
src/main/java/com/example/bankcards/exception/
├── CardNotFoundException.java           # Карта не найдена
├── UserNotFoundException.java           # Пользователь не найден
├── InsufficientFundsException.java      # Недостаточно средств
├── UsernameAlreadyTakenException.java   # Имя пользователя занято
└── SameUsernameException.java           # Тот же username
```

## 🎯 CardNotFoundException

**Назначение:** Исключение при поиске несуществующей карты

```java
public class CardNotFoundException extends RuntimeException {
    private final SearchType searchType;
    private final String value;

    public enum SearchType {
        ID
    }

    public static CardNotFoundException byId(Long id) {
        return new CardNotFoundException(SearchType.ID, id.toString());
    }
}
```

## 👤 UserNotFoundException

**Назначение:** Исключение при поиске несуществующего пользователя

```java
public class UserNotFoundException extends RuntimeException {
    private final SearchType searchType;
    private final String value;

    public enum SearchType {
        ID, USERNAME
    }

    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException(SearchType.ID, id.toString());
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException(SearchType.USERNAME, username);
    }
}
```

## 💰 InsufficientFundsException

**Назначение:** Исключение при недостаточном балансе для операции

```java
public class InsufficientFundsException extends RuntimeException {
    private final BigDecimal requestedAmount;
    private final BigDecimal currentBalance;

    public InsufficientFundsException(BigDecimal requestedAmount, BigDecimal currentBalance) {
        super(String.format(
                "Insufficient funds: requested %s, available %s",
                requestedAmount, currentBalance
        ));
        this.requestedAmount = requestedAmount;
        this.currentBalance = currentBalance;
    }
}
```

## 🔄 UsernameAlreadyTakenException

**Назначение:** Исключение при попытке использовать занятое имя пользователя

```java
public class UsernameAlreadyTakenException extends RuntimeException {
    private final String username;
    private final String errorCode;

    public UsernameAlreadyTakenException(String username) {
        super(String.format("Username %s is already taken.", username));
        this.username = username;
        this.errorCode = "USERNAME_TAKEN";
    }
}
```

## ⚠️ SameUsernameException

**Назначение:** Исключение при попытке изменить username на тот же

```java
public class SameUsernameException extends RuntimeException {
    private final String username;
    private final String errorCode;

    public SameUsernameException(String username) {
        super(String.format("New username '%s' is the same as current", username));
        this.username = username;
        this.errorCode = "SAME_USERNAME";
    }
}
```