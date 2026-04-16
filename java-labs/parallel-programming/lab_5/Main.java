package lab_5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static final int THREADS = 4;
    public static final int COUNT = 2;
    public static MySemaphoreLockCondition mySemaphore1 = new MySemaphoreLockCondition(COUNT);
    public static MySemaphoreCasSpinlock mySemaphore2 = new MySemaphoreCasSpinlock(COUNT);

    public static Semaphore regularSemaphore = new Semaphore(COUNT);

    public static void main(String[] args) {
        // System.out.println("-------------------\nRegular
        // semaphore:\n-------------------");
        // runTask(regularSemaphore);
        // System.out.println("--------------\nMy semaphore
        // LockCondition:\n--------------");
        // runTask(mySemaphore1);
        System.out.println("--------------\nMy semaphore CasSpinlock:\n--------------");
        runTask(mySemaphore2);
    }

    private static void runTask(Semaphore semaphore) {
        ExecutorService es = Executors.newFixedThreadPool(THREADS);

        List<Callable<String>> tasks = new ArrayList<>();
        List<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.println(String.format("Thread %s waits for semaphore", threadName));
                semaphore.acquire();
                System.out.println(String.format("Thread %s acquired semaphore, %d permits available",
                        threadName, semaphore.availablePermits()));
                // do something
                Thread.sleep(1000);
                System.out.println(String.format("Thread %s releases semaphore", threadName));
                semaphore.release();
                return "Thread " + threadName + " done";
            });
        }

        // invoke all the tasks
        try {
            results = es.invokeAll(tasks);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // shutdown executor service
        es.shutdown();
    }
}