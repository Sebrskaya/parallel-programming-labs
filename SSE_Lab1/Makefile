# ================= КОНФИГУРАЦИЯ =================
CC = gcc
CFLAGS = -msse -O3 -Wall -g

SSE_SRC = sse.c
BASIC_SRC = basic.c
SSE_BIN = sse
BASIC_BIN = basic

# Собираем все аргументы после имени цели в переменную ARGS
# (Это позволяет писать "make run-sse 1000" вместо "ITER=1000")
ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))

# ================= ПРАВИЛА =================

all: $(SSE_BIN) $(BASIC_BIN)

$(SSE_BIN): $(SSE_SRC)
	$(CC) $(CFLAGS) -o $@ $<

$(BASIC_BIN): $(BASIC_SRC)
	$(CC) $(CFLAGS) -o $@ $<

# Запуск с передачей аргументов
run-sse: $(SSE_BIN)
	./$(SSE_BIN) $(ARGS)

run-basic: $(BASIC_BIN)
	./$(BASIC_BIN) $(ARGS)

benchmark: $(SSE_BIN) $(BASIC_BIN)
	@echo "=== SSE (1000000 итераций) ==="
	@time ./$(SSE_BIN) 1000000
	@echo ""
	@echo "=== Basic (1000000 итераций) ==="
	@time ./$(BASIC_BIN) 1000000

clean:
	rm -f $(SSE_BIN) $(BASIC_BIN)

.PHONY: all run-sse run-basic benchmark clean

# Эта строка говорит make: "не пытайся найти файл с именем 1000"
%:
	@: