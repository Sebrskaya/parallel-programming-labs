# lab7 - MappedBus (object example)

В этой папке подготовлено решение для задания:
- использовать библиотеку `MappedBus`
- запустить object-based example (`ObjectWriter` + `ObjectReader`)
- показать работу нескольких writer-процессов и одного reader-процесса.

## Что важно по версиям Java

`MappedBus` из репозитория `caplogic/Mappedbus` использует внутренние API (`sun.nio.ch.FileChannelImpl.map0`), которые **не работают на Java 26**.

Поэтому для запуска примера нужен **Java 8** (рекомендуется), максимум старые JDK до модульных изменений.

Если у вас сейчас Java 26 по умолчанию, запускайте этот lab через Java 8:

```bat
set "JAVA8=C:\Path\To\Java8\bin\java.exe"
"%JAVA8%" -version
```

## Что уже подготовлено в lab7

- `mappedbus.jar` - скопирован из репозитория (`Mappedbus/dist/mappedbus-0.5.1.jar`)
- bat-скрипты для запуска:
  - `run-reader.bat`
  - `run-writer-0.bat`
  - `run-writer-1.bat`

## Подготовка перед запуском

1. Убедитесь, что запускаете Java 8:
```bat
java -version
```
Ожидается что-то вроде `1.8.0_xxx`.

2. Создайте директорию для файла шины (для Windows эквивалент `/tmp/test-message`):
```bat
mkdir C:\tmp
```

## Как запустить example (3 отдельных процесса)

Откройте **три** окна терминала в папке `lab7`.

Окно 1 (writer source=0):
```bat
run-writer-0.bat
```

Окно 2 (writer source=1):
```bat
run-writer-1.bat
```

Окно 3 (reader):
```bat
run-reader.bat
```

Ожидаемый формат в reader:
```text
Read: PriceUpdate [source=0, price=20, quantity=40], hasRecovered=true
Read: PriceUpdate [source=1, price=8, quantity=16], hasRecovered=true
```

## Пример ручного запуска (без точного пути)

```bat
java -cp ".;mappedbus.jar" io.mappedbus.sample.object.ObjectWriter 0
java -cp ".;mappedbus.jar" io.mappedbus.sample.object.ObjectWriter 1
java -cp ".;mappedbus.jar" io.mappedbus.sample.object.ObjectReader
```

## Кратко: как работает object sample

- `ObjectWriter` каждую секунду формирует `PriceUpdate` и пишет в memory-mapped bus.
- Первый аргумент writer (`0` или `1`) записывается в поле `source`.
- `ObjectReader` делает `reader.next()`, читает `type`, десериализует `PriceUpdate`, печатает сообщение.
- Один общий файл `/tmp/test-message` служит IPC-каналом между процессами.
