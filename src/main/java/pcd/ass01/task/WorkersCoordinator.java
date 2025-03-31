// WorkersCoordinator.java
package pcd.ass01.task;

import java.util.concurrent.CountDownLatch;

/**
 * This class coordinates the workers (tasks).
 */
public class WorkersCoordinator {
    private CountDownLatch updateLatch;
    private CountDownLatch renderLatch;

    public WorkersCoordinator(int numWorkers) {
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

    public void reset(int numWorkers) {
        this.updateLatch = new CountDownLatch(numWorkers);
        this.renderLatch = new CountDownLatch(1);
    }
}