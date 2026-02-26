#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>

int counter = 0;

void heavy_task(int thread_num)
{
    printf("\tThread #%d started\n", thread_num);

    counter++;
    printf("\t\t\tThread #%d, counter: %d\n", thread_num, counter);

    // Long-running task
    for (int i = 0; i < 1e8; i++)
    {
        sqrt(i);
    }

    printf("\tThread #%d finished\n", thread_num);
}

void sequential(int threads_num)
{
    for (int i = 0; i < threads_num; i++)
    {
        printf("MAIN: starting thread %d\n", i);
        heavy_task(i);
    }
}

int main(int argc, char **argv)
{
    if (argc < 2)
    {
        fprintf(stderr, "Usage: %s <threads_num>\n", argv[0]);
        return 1;
    }
    int threads_num = atoi(argv[1]);

    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);

    sequential(threads_num);

    clock_gettime(CLOCK_MONOTONIC, &end);
    double time_spent = (end.tv_sec - start.tv_sec) +
                        (end.tv_nsec - start.tv_nsec) / 1e9;
    printf("Time: %.6f seconds\n", time_spent);
    return 0;
}