```markdown
# Миграции базы данных

Миграции Liquibase для управления схемой базы данных.

## 📁 Структура миграций

```
src/main/resources/db/migration/
└── V1_create_tables.yaml          # Первоначальная схема базы данных
```

## 🗃️ V1_create_tables.yaml

**Назначение:** Создание первоначальной схемы базы данных и начальных данных

### Таблица users

```yaml
- createTable:
    tableName: users
    columns:
      - column:
          name: id
          type: BIGINT
          autoIncrement: true
          constraints:
            primaryKey: true
            nullable: false
      - column:
          name: username
          type: VARCHAR(50)
          constraints:
            unique: true
            nullable: false
      - column:
          name: password
          type: VARCHAR(255)
          constraints:
            nullable: false
      - column:
          name: role
          type: VARCHAR(20)
          constraints:
            nullable: false
```

**Структура:**
- `id` - первичный ключ, автоинкремент
- `username` - уникальное имя пользователя
- `password` - зашифрованный пароль
- `role` - роль пользователя (USER/ADMIN)

### Таблица cards

```yaml
- createTable:
    tableName: cards
    columns:
      - column:
          name: id
          type: BIGINT
          autoIncrement: true
          constraints:
            primaryKey: true
            nullable: false
      - column:
          name: card_number_encrypted
          type: TEXT
          constraints:
            nullable: false
      - column:
          name: owner_name
          type: VARCHAR(100)
          constraints:
            nullable: false
      - column:
          name: expiry_date
          type: DATE
          constraints:
            nullable: false
      - column:
          name: status
          type: VARCHAR(20)
          constraints:
            nullable: false
      - column:
          name: balance
          type: DECIMAL(19, 2)
          defaultValue: 0
          constraints:
            nullable: false
      - column:
          name: user_id
          type: BIGINT
          constraints:
            nullable: false
    constraints:
      - foreignKey:
          constraintName: fk_card_user
          referencedTableName: users
          referencedColumnNames: id
          columnNames: user_id
```

**Структура:**
- `id` - первичный ключ, автоинкремент
- `card_number_encrypted` - зашифрованный номер карты
- `owner_name` - имя владельца карты
- `expiry_date` - срок действия карты
- `status` - статус карты (ACTIVE/BLOCKED/EXPIRED)
- `balance` - баланс карты с точностью до 2 знаков
- `user_id` - внешний ключ на таблицу users

### Начальные данные

```yaml
- insert:
    tableName: users
    columns:
      - column:
          name: username
          value: admin
      - column:
          name: password
          value: $2a$12$5V8uJ9O6p2qQ7w8K9v0YZeR8L9M1N2B3C4D5E6F7G8H9I0J1K2L3M4N5O6P
      - column:
          name: role
          value: ROLE_ADMIN
```

**Администратор по умолчанию:**
- **Username:** admin
- **Password:** (зашифрованный пароль)
- **Role:** ROLE_ADMIN

## 🔧 Особенности миграции

### Предусловия (Preconditions)
- Проверка существования таблиц перед созданием
- Проверка отсутствия администратора перед вставкой
- `onFail: MARK_RAN` - пропуск если условие не выполнено

### Ограничения (Constraints)
- Первичные ключи для идентификации записей
- Уникальные ограничения для username
- Внешние ключи для целостности данных
- Ограничения NOT NULL для обязательных полей

### Типы данных
- `BIGINT` для идентификаторов
- `VARCHAR` для строковых данных
- `TEXT` для зашифрованных номеров карт
- `DATE` для дат
- `DECIMAL(19,2)` для финансовых операций

## 🚀 Применение миграций

Миграции автоматически применяются при запуске приложения через Liquibase:

```properties
spring.liquibase.change-log=classpath:db/migration/changelog-master.yaml
spring.liquibase.enabled=true
```

## 📊 Схема базы данных

```
users
├── id (BIGINT, PK)
├── username (VARCHAR(50), UNIQUE)
├── password (VARCHAR(255))
└── role (VARCHAR(20))

cards
├── id (BIGINT, PK)
├── card_number_encrypted (TEXT)
├── owner_name (VARCHAR(100))
├── expiry_date (DATE)
├── status (VARCHAR(20))
├── balance (DECIMAL(19,2))
└── user_id (BIGINT, FK → users.id)
```

## 🛡️ Безопасность

- Пароли хранятся в зашифрованном виде
- Номера карт шифруются перед сохранением
- Внешние ключи обеспечивают целостность данных
- Ограничения NOT NULL предотвращают частичные данные
