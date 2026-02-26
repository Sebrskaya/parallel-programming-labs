# ================= КОНФИГУРАЦИЯ =================
CC = gcc
# -pthread : Обязательно для компиляции и линковки потоков
# -lm      : Подключение математической библиотеки (для sqrt)
# -O3      : Оптимизация
# -Wall -g : Предупреждения и отладка
CFLAGS = -pthread -O3 -Wall -g
LDFLAGS = -lm  # Выносим библиотеки отдельно

# Исходные файлы
PTHREADS_SRC = pthreads.c
BASIC_SRC = basic.c

# Исполняемые файлы
PTHREADS_BIN = pthreads
BASIC_BIN = basic

# Собираем все аргументы после имени цели в переменную ARGS
# (Позволяет писать "make run-pthreads 4" вместо "THREADS=4")
ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

# ================= ПРАВИЛА =================

# Цель по умолчанию: собрать всё
all: $(PTHREADS_BIN) $(BASIC_BIN)

# Сборка Pthreads версии
$(PTHREADS_BIN): $(PTHREADS_SRC)
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)  

# Сборка Sequential версии
$(BASIC_BIN): $(BASIC_SRC)
	$(CC) $(CFLAGS) -o $@ $< $(LDFLAGS)                

# Запуск с передачей аргументов (число потоков)
run-pthreads: $(PTHREADS_BIN)
	./$(PTHREADS_BIN) $(ARGS)

run-basic: $(BASIC_BIN)
	./$(BASIC_BIN) $(ARGS)

# Бенчмарк: сравниваем 4 потока vs 1 поток
benchmark: $(PTHREADS_BIN) $(BASIC_BIN)
	@echo "=== Pthreads (4 потока) ==="
	@time ./$(PTHREADS_BIN) 4
	@echo ""
	@echo "=== Sequential (1 поток) ==="
	@time ./$(BASIC_BIN) 1

# Очистка проекта
clean:
	rm -f $(PTHREADS_BIN) $(BASIC_BIN)

# Помощь
help:
	@echo "Доступные команды:"
	@echo "  make                    - Скомпилировать обе версии"
	@echo "  make run-pthreads N     - Запустить pthreads с N потоками"
	@echo "  make run-sequential N   - Запустить sequential с N 'потоками'"
	@echo "  make benchmark          - Сравнить производительность (4 vs 1)"
	@echo "  make clean              - Удалить исполняемые файлы"
	@echo "  make help               - Показать эту справку"

.PHONY: all run-pthreads run-sequential benchmark clean help

# Эта строка говорит make: "не пытайся найти файл с именем аргумента"
%:
	@: