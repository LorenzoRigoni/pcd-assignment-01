package pcd.ass01.virtualThreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages the workers after the update of positions of the boids.
 */
public class WorkersCoordinator {
    private final ReentrantLock lock;
    private final Condition condition;
    private final int numWorkers;
    private int countWorkersFinished;

    public WorkersCoordinator(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersFinished = 0;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
    }

    /**
     * When a worker finish the job (update velocities and positions of the boids),
     * it checks if all the workers have done. If it's true, allows to repaint.
     * Finally, it stops threads until every boid has been repainted.
     */
    public void workerDone() {
        this.lock.lock();
        try {
            this.countWorkersFinished++;
            if (this.countWorkersFinished == this.numWorkers)
                this.condition.signalAll();

            while(this.countWorkersFinished != 0) {
                this.condition.await();
            }
        } catch (InterruptedException e) {}
        finally {
            this.lock.unlock();
        }
    }

    /**
     * Check if all the workers have done their work (update velocities and positions of the boids).
     */
    public void waitWorkers() {
        this.lock.lock();
        try {
            while(this.countWorkersFinished < this.numWorkers) {
               this.condition.await();
            }
        } catch (InterruptedException e) {}
        finally {
            this.lock.unlock();
        }
    }

    /**
     * When all the boids has been repainted, allows threads to restart with updates.
     */
    public void coordinatorDone() {
        this.lock.lock();
        try{
            this.countWorkersFinished = 0;
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }
}
