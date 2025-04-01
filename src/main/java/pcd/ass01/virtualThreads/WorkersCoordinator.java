package pcd.ass01.virtualThreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WorkersCoordinator {
    private final int numWorkers;
    private int countWorkersFinished;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public WorkersCoordinator(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersFinished = 0;
    }

    /**
     * When a worker finish the job (update velocities and positions of the boids),
     * it checks if all the workers have done. If it's true, allows to repaint.
     * Finally, it stops threads until every boid has been repainted.
     */
    public void workerDone() {
        lock.lock();
        try {
            this.countWorkersFinished++;
            //System.out.println("Worker finito. Count = " + this.countWorkersFinished);
            if (this.countWorkersFinished == this.numWorkers)
                condition.signalAll();

            while(this.countWorkersFinished != 0) {
                condition.await();
            }
        } catch (InterruptedException e) {}
        finally {
            lock.unlock();
        }
    }

    /**
     * Check if all the workers have done their work (update velocities and positions of the boids).
     */
    public void waitWorkers() {
        lock.lock();
        try {
            while(this.countWorkersFinished < this.numWorkers) {
                //System.out.println("Aspetto che tutti i workers abbiano finito. Count = " + this.countWorkersFinished);
               condition.await();
            }
        } catch (InterruptedException e) {}
        finally {
            lock.unlock();
        }
    }

    /**
     * When all the boids has been repainted, allows threads to restart with updates.
     */
    public void coordinatorDone() {
        lock.lock();
        try{
            //System.out.println("Il coordinatore sta azzerando il contatore.");
            this.countWorkersFinished = 0;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
