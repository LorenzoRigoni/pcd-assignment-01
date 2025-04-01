package pcd.ass01.task;

import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages the barrier for the threads after the update of velocities.
 */
public class UpdateLatch {
    private int count;
    private final ReentrantLock lock = new ReentrantLock();

    public UpdateLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
        this.count = count;
    }
    
    public synchronized void await() {
        lock.lock();
        try {
            while (count > 0) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            lock.unlock();
        }
    }

    public synchronized void countDown() {
        lock.lock();
        try {
            if (count > 0) {
                count--;
                if (count == 0) {
                    notifyAll(); // Notify all waiting threads when count reaches zero
                }
            } else {
                throw new IllegalStateException("Count has already reached zero");
            }
        } finally {
            lock.unlock();
        }
        
    }

    public synchronized int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
