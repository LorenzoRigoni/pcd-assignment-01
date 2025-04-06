package pcd.ass01.common;

/**
 * This class manages the state of the simulation.
 */
public class SimulationState {
    private boolean isStopped;
    private boolean isPaused;

    public SimulationState() {
        this.isStopped = true;
        this.isPaused = false;
    }

    /**
     * Checks if the simulation is stopped.
     *
     * @return true if it is stopped, false otherwise
     */
    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Checks if the simulation is paused.
     *
     * @return true if it is paused, false otherwise
     */
    public synchronized boolean isPaused() {
        return this.isPaused;
    }

    /**
     * Starts the simulation.
     */
    public synchronized void startSimulation() {
        this.isStopped = false;
        this.isPaused = false;
        notifyAll();
    }

    /**
     * Suspends the simulation.
     */
    public synchronized void suspendSimulation() {
        this.isPaused = true;
    }

    /**
     * Resumes the simulation.
     */
    public synchronized void resumeSimulation() {
        if (!this.isStopped) {
            this.isPaused = false;
            notifyAll();
        }
    }

    /**
     * Stops the simulation.
     */
    public synchronized void stopSimulation() {
        this.isStopped = true;
        this.isPaused = true;
    }

    /**
     * Stops the thread until the simulation is stopped or paused.
     */
    public synchronized void waitForSimulation() {
        try {
            while (this.isStopped || this.isPaused) {
                wait();
            }
        } catch (InterruptedException e) {}
    }
}
