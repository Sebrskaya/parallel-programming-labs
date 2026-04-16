# MappedBus - разбор проекта (коротко)

Репозиторий: https://github.com/caplogic/Mappedbus

## Структура

- `src/main/io/mappedbus`
  - ядро библиотеки (`MappedBusWriter`, `MappedBusReader`, `MemoryMappedFile`, константы/интерфейс сообщений)
- `src/sample/io/mappedbus/sample`
  - `object` - object-based reader/writer (`PriceUpdate`)
  - `bytearray` - byte-array based reader/writer
  - `token` - пример передачи токена между узлами
- `src/perf/io/mappedbus/perf`
  - нагрузочный тест на пропускную способность/latency
- `test/io/mappedbus`
  - integrity и unit тесты (`RollbackTest`, `TokenTest`, reader/writer/mmap tests)
- `build.xml`
  - сборка через Ant (`compile`, `test`, `dist`)
- `dist`
  - готовые jar-артефакты (`mappedbus-0.5.1.jar`)

## Ядро алгоритма

- В начале файла хранится `limit` (сколько данных выделено writer'ами).
- Writer делает атомарный `getAndAddLong` по `limit`, резервируя запись.
- После записи payload writer выставляет status-флаг `Commit` через CAS.
- Reader читает `limit`, двигается по записям, обрабатывает только `Commit`.
- Если запись долго в `NotSet`, reader помечает её `Rollback` и пропускает.

## Object example

- Сообщение: `PriceUpdate {source, price, quantity}`.
- `ObjectWriter <source>` пишет по одному сообщению в секунду в `/tmp/test-message`.
- `ObjectReader` постоянно читает и печатает `Read: PriceUpdate [...]`.
- При двух writer-процессах reader видит общий поток сообщений от обоих источников.

## Практический вывод для lab7

- Example корректно воспроизводится при запуске 2 writer + 1 reader.
- На Java 26 требуется откат к Java 8 для исходной версии библиотеки без модификаций.
