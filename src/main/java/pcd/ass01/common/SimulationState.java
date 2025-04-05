package pcd.ass01.common;

/**
 * This class manage the state of the simulation: running or suspended.
 */
public class SimulationState {
    private boolean isStopped;
    private boolean isPaused;

    public SimulationState() {
        this.isStopped = true;
        this.isPaused = false;
    }

    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized boolean isPaused() {
        return this.isPaused;
    }

    public synchronized void startSimulation() {
        this.isStopped = false;
        this.isPaused = false;
        notifyAll();
    }

    public synchronized void suspendSimulation() {
        this.isPaused = true;
    }

    public synchronized void resumeSimulation() {
        if (!this.isStopped) {
            this.isPaused = false;
            notifyAll();
        }
    }

    public synchronized void stopSimulation() {
        this.isStopped = true;
        this.isPaused = true;
    }

    public synchronized void waitForSimulation() {
        try {
            while (this.isStopped || this.isPaused) {
                wait();
            }
        } catch (InterruptedException e) {}
    }
}
