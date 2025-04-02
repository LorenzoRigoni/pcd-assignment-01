package pcd.ass01.task;

/**
 * This class manages the latch for the tasks.
 */
public class UpdateLatch {
    private int count;

    public UpdateLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
        this.count = count;
    }

    /**
     * Stops the thread until the counter is greater than 0.
     */
    public synchronized void await() {
        try {
            while (count > 0) {
                wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Decrements the counter and checks if the counter is equal to 0.
     */
    public synchronized void countDown() {
        if (count > 0) {
            count--;
            if (count == 0) {
                notifyAll();
            }
        }
    }
}
