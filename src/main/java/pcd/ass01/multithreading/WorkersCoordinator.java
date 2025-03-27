package pcd.ass01.multithreading;

/**
 * This class coordinates the workers (threads).
 */
public class WorkersCoordinator {
    private final int numWorkers;
    private int countWorkersFinished;

    public WorkersCoordinator(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersFinished = 0;
    }

    /**
     * When a worker finish the job (update velocities and positions of the boids),
     * it checks if all the workers have done. If it's true, allows to repaint.
     * Finally, it stops threads until every boid has been repainted.
     */
    public synchronized void workerDone() {
        try {
            this.countWorkersFinished++;
            if (this.countWorkersFinished == this.numWorkers)
                notifyAll();

            while(this.countWorkersFinished != 0) {
                wait();
            }
        } catch (InterruptedException e) {}
    }

    /**
     * Check if all the workers have done their work (update velocities and positions of the boids).
     */
    public synchronized void waitWorkers() {
        try {
            while(this.countWorkersFinished < this.numWorkers) {
                wait();
            }
        } catch (InterruptedException e) {}
    }

    /**
     * When all the boids has been repainted, allows threads to restart with updates.
     */
    public synchronized void coordinatorDone() {
        this.countWorkersFinished = 0;
        notifyAll();
    }
}
