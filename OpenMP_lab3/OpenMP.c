#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <omp.h>
#include <pthread.h>
#include <time.h>

void heavy_task()
{
    int limit = 1e8;
    volatile double result = 0;
    for (int i = 0; i < limit; i++)
    {
        result += sqrt(i);
    }
}

void openmp(int thread_num)
{
#pragma omp parallel for num_threads(thread_num)
    for (int i = 0; i < thread_num; i++)
    {
        heavy_task();
    }
}

void *pthread_task(void *arg)
{
    heavy_task();
    return NULL;
}

// Реализация pthreads
void pthreads_run(int thread_num)
{
    pthread_t threads[thread_num];

    // Создание потоков
    for (int i = 0; i < thread_num; i++)
    {
        if (pthread_create(&threads[i], NULL, pthread_task, NULL) != 0)
        {
            perror("pthread_create");
            exit(1);
        }
    }

    // Ожидание завершения
    for (int i = 0; i < thread_num; i++)
    {
        pthread_join(threads[i], NULL);
    }
}

// Последовательная версия
void sequential(int thread_num)
{
    for (int i = 0; i < thread_num; i++)
    {
        heavy_task();
    }
}

int main(int argc, char **argv)
{
    if (argc < 2)
    {
        printf("Usage: %s <threads>\n", argv[0]);
        return 1;
    }

    int thread_num = atoi(argv[1]);

    double start, end;

    // ===== Sequential =====
    start = omp_get_wtime();
    sequential(thread_num);
    end = omp_get_wtime();
    printf("=== Sequential ===\n");
    printf("Time: %.6f seconds\n\n", end - start);

    // ===== OpenMP =====
    start = omp_get_wtime();
    openmp(thread_num);
    end = omp_get_wtime();
    printf("=== OpenMP (%d threads) ===\n", thread_num);
    printf("Time: %.6f seconds\n", end - start);

    // ===== Pthreads =====
    start = omp_get_wtime();
    pthreads_run(thread_num);
    end = omp_get_wtime();
    printf("=== Pthreads (%d threads) ===\n", thread_num);
    printf("Time: %.6f seconds\n\n", end - start);

    return 0;
}