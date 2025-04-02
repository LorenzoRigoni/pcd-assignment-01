package pcd.ass01.virtualThreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages the barrier for the threads.
 */
public class UpdateBarrier {
    private final int numWorkers;
    private int countWorkersAtBarrier;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public UpdateBarrier(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersAtBarrier = 0;
    }

    /**
     * Stops the threads until all the boids have updated their velocities.
     */
    public void waitBarrier() {
        lock.lock();
        try {
            this.countWorkersAtBarrier++;
            if (this.countWorkersAtBarrier < this.numWorkers)
                condition.await();
            else {
                this.countWorkersAtBarrier = 0;
                condition.signalAll();
            }
        } catch (InterruptedException e) {}
        finally {
            lock.unlock();
        }
    }
}
