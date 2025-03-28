package pcd.ass01.virtualThreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manage the state of the simulation: running or suspended.
 */

public class SimulationState {

    private boolean isRunning;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public SimulationState(boolean isRunning) {
        lock.lock();
        try{
            this.isRunning = isRunning;
        } finally {
            lock.unlock();
        }

    }

    /**
     * Give the state of the simulation.
     *
     * @return true if the simulation is running, false otherwise
     */
    public boolean isRunning() {
        lock.lock();
        try{
            return this.isRunning;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Suspend the simulation.
     */
    public void suspendSimulation() {
        lock.lock();
        try{
            this.isRunning = false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resume the simulation.
     */
    public void resumeSimulation() {
        lock.lock();
        try{
            this.isRunning = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stop all the threads until the simulation is suspended.
     */
    public void waitForSimulation() {
        lock.lock();
        try {
            while (!this.isRunning) {
                condition.await();
            }
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

}
