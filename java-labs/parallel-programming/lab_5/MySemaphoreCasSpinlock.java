package lab_5;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MySemaphoreCasSpinlock extends Semaphore {

    private int max;
    private final AtomicInteger permits;

    public MySemaphoreCasSpinlock(int initialPermits) {
        super(initialPermits);
        permits = new AtomicInteger(initialPermits);
    }

    @Override
    public void acquire() throws InterruptedException {
        while (true) {
            int current = permits.get();
            if (current == 0) {
                Thread.yield(); // ждём, пока появится разрешение
                continue;
            }
            if (permits.compareAndSet(current, current - 1)) {
                return;
            }
            // если CAS не прошёл — повторяем цикл
        }
    }

    @Override
    public void release() {
        // ToDo: написать код
        while (true) {
            int current = permits.get();
            if (permits.compareAndSet(current, current + 1)) {
                break;
            }
        }
    }

    @Override
    public int availablePermits() {
        return permits.get();
    }
}