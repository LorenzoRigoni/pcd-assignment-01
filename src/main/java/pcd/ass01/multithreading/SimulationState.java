package pcd.ass01.multithreading;

public class SimulationState {
    private boolean isRunning;

    public SimulationState(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void suspendSimulation() {
        this.isRunning = false;
    }

    public synchronized void resumeSimulation() {
        this.isRunning = true;
        notifyAll();
    }

    public synchronized void waitForSimulation() {
        try {
            while (!this.isRunning) {
                wait();
            }
        } catch (InterruptedException e) {}
    }
}
