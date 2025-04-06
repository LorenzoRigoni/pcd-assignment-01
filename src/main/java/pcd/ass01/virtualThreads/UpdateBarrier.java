package pcd.ass01.virtualThreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages the barrier for the workers.
 */
public class UpdateBarrier {
    private final ReentrantLock lock;
    private final Condition condition;
    private final int numWorkers;
    private int countWorkersAtBarrier;

    public UpdateBarrier(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersAtBarrier = 0;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
    }

    /**
     * Stops the workers until all the boids have updated their velocities.
     */
    public void waitBarrier() {
        this.lock.lock();
        try {
            this.countWorkersAtBarrier++;
            if (this.countWorkersAtBarrier < this.numWorkers)
                this.condition.await();
            else {
                this.countWorkersAtBarrier = 0;
                this.condition.signalAll();
            }
        } catch (InterruptedException e) {}
        finally {
            this.lock.unlock();
        }
    }
}
