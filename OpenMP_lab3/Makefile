CC = gcc
CFLAGS = -O2 -Wall -march=native
LDFLAGS = -lm

TARGET = OpenMP
THREADS ?= 4

all: $(TARGET)

$(TARGET): OpenMP.c
	$(CC) $(CFLAGS) -fopenmp OpenMP.c -o $(TARGET) -lpthread $(LDFLAGS)

run:
	./$(TARGET) $(THREADS)

clean:
	rm -f $(TARGET)