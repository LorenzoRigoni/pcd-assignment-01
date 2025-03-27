package pcd.ass01.multithreading;

/**
 * This class manage the state of the simulation: running or suspended.
 */
public class SimulationState {
    private boolean isRunning;

    public SimulationState(boolean isRunning) {
        this.isRunning = isRunning;
    }

    /**
     * Give the state of the simulation.
     *
     * @return true if the simulation is running, false otherwise
     */
    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    /**
     * Suspend the simulation.
     */
    public synchronized void suspendSimulation() {
        this.isRunning = false;
    }

    /**
     * Resume the simulation.
     */
    public synchronized void resumeSimulation() {
        this.isRunning = true;
        notifyAll();
    }

    /**
     * Stop all the threads until the simulation is suspended.
     */
    public synchronized void waitForSimulation() {
        try {
            while (!this.isRunning) {
                wait();
            }
        } catch (InterruptedException e) {}
    }
}
