package pcd.ass01.multithreading;

/**
 * This class manages the barrier for the threads after the update of velocities.
 */
public class UpdateBarrier {
    private final int numWorkers;
    private int countWorkersAtBarrier;

    public UpdateBarrier(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersAtBarrier = 0;
    }

    /**
     * Stops the threads until all the boids have updated their velocities.
     */
    public synchronized void waitBarrier() {
        try {
            this.countWorkersAtBarrier++;
            if (this.countWorkersAtBarrier < this.numWorkers)
                wait();
            else {
                this.countWorkersAtBarrier = 0;
                notifyAll();
            }
        } catch (InterruptedException e) {}
    }
}
