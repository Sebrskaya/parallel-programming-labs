#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <time.h>

int counter = 0;

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

void *heavy_task(void *i)
{
    int thread_num = *((int *)i);

    // Пример критической секции с мьютексом
    // printf("\tThread #%d started\n", thread_num);
    // pthread_mutex_lock(&mutex);
    // printf("\t\tThread #%d acquired mutex\n", thread_num);
    // counter++;
    // printf("\t\t\tThread #%d, counter: %d\n", thread_num, counter);
    // printf("\t\tThread #%d released mutex\n", thread_num);
    // pthread_mutex_unlock(&mutex);

    // Long-running task
    for (int i = 0; i < 1e8; i++)
    {
        sqrt(i);
    }

    printf("\tThread #%d finished\n", thread_num);
    free(i);

    return NULL;
}

void pthreads(int threads_num)
{

    pthread_t threads[threads_num];
    int status;

    for (int i = 0; i < threads_num; i++)
    {

        printf("MAIN: starting thread %d\n", i);

        int *thread_num = (int *)malloc(sizeof(int));
        *thread_num = i;

        status = pthread_create(&threads[i], NULL, heavy_task, thread_num);

        if (status != 0)
        {
            fprintf(stderr, "pthread_create failed, error code %d\n", status);
            exit(EXIT_FAILURE);
        }
    }

    for (int i = 0; i < threads_num; i++)
    {
        pthread_join(threads[i], NULL);
    }
}

int main(int argc, char **argv)
{
    int threads_num = atoi(argv[1]);

    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);
    pthreads(threads_num);
    clock_gettime(CLOCK_MONOTONIC, &end);
    pthread_mutex_destroy(&mutex);

    double time_spent = (end.tv_sec - start.tv_sec) +
                        (end.tv_nsec - start.tv_nsec) / 1e9;
    printf("Time: %.6f seconds\n", time_spent);
    return 0;
}