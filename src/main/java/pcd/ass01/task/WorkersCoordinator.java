package pcd.ass01.task;

import java.util.concurrent.CountDownLatch;

public class WorkersCoordinator {
    private volatile CountDownLatch updateLatch;
    private volatile CountDownLatch renderLatch;
    private final int numThreads;

    public WorkersCoordinator(int numWorkers) {
        this.numThreads = numWorkers;
        this.updateLatch = new CountDownLatch(numWorkers);
        this.renderLatch = new CountDownLatch(1);
    }

    public void workerDone() {
        updateLatch.countDown();
    }

    public void waitWorkers() throws InterruptedException {
        updateLatch.await();
    }

    public void coordinatorDone() {
        renderLatch.countDown();
    }

    public void waitForRender() throws InterruptedException {
        renderLatch.await();
    }

    public synchronized void reset() {
        // Only reset if all workers have completed
        if (updateLatch.getCount() == 0) {
            this.updateLatch = new CountDownLatch(numThreads);
            this.renderLatch = new CountDownLatch(1);
        }
    }
}