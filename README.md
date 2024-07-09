
# ShortcutService API

## Обзор

<p align="center">
  <img src="logo.gif"/>
</p>

ShortcutService - это API, написанное на Scala, предназначенное для управления горячими клавишами. Оно позволяет добавлять новые горячие клавиши и получать горячие клавиши по категориям. API сопровождается модульными и интеграционными тестами для обеспечения надежной функциональности.

## Содержание
- [Начало работы](#начало-работы)
- [Конечные точки](#конечные-точки)
    - [Добавить горячую клавишу](#добавить-горячую-клавишу)
    - [Получить горячие клавиши по категории](#получить-горячие-клавиши-по-категории)
- [Тестирование](#тестирование)
- [Зависимости](#зависимости)
- [Используемые библиотеки](#используемые-библиотеки)

## Начало работы
### Необходимые условия
- Scala 3.4.2
- sbt (Simple Build Tool)

### Установка
1. Клонируйте репозиторий:
    ```bash
    git clone https://github.com/Xtoniab/ShortcutApiScala.git
    cd ShortcutService
    ```

2. Запустите приложение:
    ```bash
    sbt run
    ```

### Запуск тестов
Для запуска тестов используйте следующую команду:
```bash
sbt test
```

## Конечные точки
### Добавить горячую клавишу
**Конечная точка:** `POST /add`

**Описание:** Добавляет новую горячую клавишу в сервис.

**Тело запроса:**
```json

{
  "binding": "Ctrl + Shift + K",
  "description": "Push current branch to remote repository",
  "action": "git.push"
}
```

**Ответы:**
- `200 OK`: Горячая клавиша успешно добавлена.
  ```json
  {
    "success": true
  }
  ```
- `400 Bad Request`: Не удалось добавить горячую клавишу.
  ```json
  {
    "success": false,
    "error":  "string"
  }
  ```

### Получить горячие клавиши по категории
**Конечная точка:** `GET /category/{categoryName}`

**Описание:** Получает горячие клавиши по указанной категории.

**Параметры:**
- `categoryName`: Категория горячих клавиш, которые нужно получить.

**Ответы:**
- `200 OK`: Возвращает список горячих клавиш.
  ```json
  [
    {
      "actionName": "string",
      "binding": "string"
    }
  ]
  ```

## Тестирование
Этот проект включает как модульные, так и интеграционные тесты для обеспечения функциональности API.

### ShortcutServiceTests & ShortcutRegexTests 
Эти тесты охватывают основную логику `ShortcutService` и его компонентов.

### ShortcutServiceIntegrationTests
Эти тесты проверяют сквозную функциональность конечных точек API, обеспечивая работу интеграции между компонентами.

## Используемые библиотеки
- **logback-classic**: библиотека для логирования, обеспечивающая высокую производительность и гибкость.
- **cats-effect**: библиотека для работы с эффектами в функциональном программировании, обеспечивающая удобные и мощные абстракции для работы с асинхронными и побочными эффектами.
- **http4s-dsl**: DSL для создания HTTP серверов и клиентов на Scala, обеспечивающий удобный синтаксис и мощные возможности.
- **http4s-blaze-server**: реализация HTTP сервера на базе Blaze, обеспечивающая высокую производительность и асинхронность.
- **http4s-circe**: интеграция http4s с Circe для работы с JSON, обеспечивающая удобный синтаксис и мощные возможности для сериализации и десериализации JSON.
- **circe-generic**: библиотека для автоматической генерации JSON кодеков для case классов, обеспечивающая удобный синтаксис и высокую производительность.
- **circe-parser**: библиотека для парсинга JSON, обеспечивающая удобный синтаксис и высокую производительность.
- **scalatest**: библиотека для модульного тестирования на Scala, обеспечивающая удобный синтаксис и мощные возможности для написания тестов.
- **http4s-blaze-client**: реализация HTTP клиента на базе Blaze, обеспечивающая высокую производительность и асинхронность.

---