// SimulationState.java
package pcd.ass01.task;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manages the state of the simulation: running or suspended.
 */
public class SimulationState {
    private boolean isRunning;

    public SimulationState(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void suspendSimulation() {
        this.isRunning=false;
    }

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