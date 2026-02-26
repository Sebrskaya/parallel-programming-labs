#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void algorithm(float a[], float b[], volatile float c[])
{
    for (int i = 0; i < 4; i++)
    {
        c[i] = a[i] * b[i];
    }

    for (int i = 0; i < 4; i++)
    {
        // printf("%f ", c[i]);
    }
    // printf("\n");
}

int main(int argc, char **argv)
{

    int iterations = 1;
    if (argc > 1)
    {
        iterations = atoi(argv[1]);
    }

    float a[4] = {40.0f, 200.0f, 50.0f, 300.0f};
    float b[4] = {10.0f, 25.0f, 100.0f, 15.0f};
    volatile float c[4] = {0};

    printf("Running %d iterations...\n", iterations);

    clock_t start = clock();

    for (int i = 0; i < iterations; i++)
    {
        algorithm(a, b, c);
    }

    clock_t end = clock();
    double time_spent = (double)(end - start) / CLOCKS_PER_SEC;

    printf("=== SSE ===\n");
    printf("Result: %.2f %.2f %.2f %.2f\n", c[0], c[1], c[2], c[3]);
    printf("Time: %.6f seconds\n", time_spent);

    return 0;
}