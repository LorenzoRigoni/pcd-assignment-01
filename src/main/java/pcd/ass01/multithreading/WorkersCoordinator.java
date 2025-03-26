package pcd.ass01.multithreading;

public class WorkersCoordinator {
    private final int numWorkers;
    private int countWorkersFinished;

    public WorkersCoordinator(int numWorkers) {
        this.numWorkers = numWorkers;
        this.countWorkersFinished = 0;
    }

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

    public synchronized void waitWorkers() {
        try {
            while(this.countWorkersFinished < this.numWorkers) {
                wait();
            }
        } catch (InterruptedException e) {}
    }

    public synchronized void coordinatorDone() {
        this.countWorkersFinished = 0;
        notifyAll();
    }
}
