```markdown
# Утилиты

Вспомогательные классы: шифрование, маскирование и прочее.

## 📁 Структура утилит

```
src/main/java/com/example/bankcards/util/
└── CardNumberEncryptor.java     # Шифрование и маскирование номеров карт
```

## 🔐 CardNumberEncryptor

**Назначение:** Шифрование номеров карт для безопасного хранения и маскирование для отображения

```java
@Component
public class CardNumberEncryptor {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private static final String SECRET_KEY = "0123456789abcdef0123456789abcdef";
}
```

### Константы безопасности
- **ALGORITHM**: AES/GCM/NoPadding
- **KEY_SIZE**: 256 бит
- **IV_LENGTH**: 12 байт
- **TAG_LENGTH**: 128 бит
- **SECRET_KEY**: статический ключ шифрования

## 🔒 Методы шифрования

### encrypt(String cardNumber)
**Назначение:** Шифрование номера карты перед сохранением в БД

```java
public String encrypt(String cardNumber) {
    // Реализация шифрования
}
```

**Процесс шифрования:**
1. Генерация случайного IV
2. Создание спецификации ключа и параметров GCM
3. Шифрование данных номера карты
4. Объединение IV и зашифрованных данных
5. Кодирование в Base64

### decrypt(String encryptedData)
**Назначение:** Дешифрование номера карты для маскирования

```java
public String decrypt(String encryptedData) {
    // Реализация дешифрования
}
```

**Процесс дешифрования:**
1. Декодирование Base64 строки
2. Извлечение IV и зашифрованных данных
3. Инициализация cipher в режиме дешифрования
4. Дешифрование данных
5. Возврат оригинального номера карты

## 🎭 Маскирование данных

### maskCardNumber(String fullNumber)
**Назначение:** Маскирование номера карты для безопасного отображения

```java
public static String maskCardNumber(String fullNumber) {
    if (fullNumber == null || fullNumber.length() < 4) return "****";
    String lastFour = fullNumber.substring(fullNumber.length() - 4);
    return "**** **** **** " + lastFour;
}
```

**Формат маскирования:**
- Вход: `4111111111111234`
- Выход: `**** **** **** 1234`

## 🛡️ Безопасность

### Алгоритм AES-GCM
- Аутентифицированное шифрование
- Случайный IV
- Длина ключа 256 бит

## 🔄 Использование в сервисах

### CardService
```
// Шифрование при создании карты
String encrypted = encryptor.encrypt(createDTO.getCardNumberPlain());
card.setCardNumberEncrypted(encrypted);

// Дешифрование и маскирование для ответа
String decrypted = encryptor.decrypt(card.getCardNumberEncrypted());
dto.setMaskedCardNumber(CardNumberEncryptor.maskCardNumber(decrypted));
```

**Принцип работы:**
- Номер карты шифруется при сохранении в БД
- Дешифруется только для маскирования при отображении
- Оригинальный номер никогда не передается клиенту
```