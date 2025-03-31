// SimulationState.java
package pcd.ass01.task;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manages the state of the simulation: running or suspended.
 */
public class SimulationState {
    private final AtomicBoolean isRunning;

    public SimulationState(boolean isRunning) {
        this.isRunning = new AtomicBoolean(isRunning);
    }

    public boolean isRunning() {
        return this.isRunning.get();
    }

    public void suspendSimulation() {
        this.isRunning.set(false);
    }

    public void resumeSimulation() {
        this.isRunning.set(true);
    }
}