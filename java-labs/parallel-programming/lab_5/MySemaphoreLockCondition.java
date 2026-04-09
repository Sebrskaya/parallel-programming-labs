package lab_5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphoreLockCondition extends Semaphore {

    private int max;
    private final ReentrantLock lock = new ReentrantLock(); // Reeentran lock + условные переменные(condition) и
                                                            // CAS+spinlock
    private final Condition permitsAvailable = lock.newCondition();
    private int permits;

    public MySemaphoreLockCondition(int initialPermits) {
        super(initialPermits);
        lock.lock();
        try {
            permits = initialPermits;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void acquire() throws InterruptedException {
        // ToDo: написать код ()
        lock.lock();
        try {
            while (permits == 0) {
                permitsAvailable.await();
            }
            permits--;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release() {
        // ToDo: написать код
        lock.lock();
        permits++;
        permitsAvailable.signal();
        lock.unlock();
    }

    @Override
    public int availablePermits() {
        return permits;
    }
}