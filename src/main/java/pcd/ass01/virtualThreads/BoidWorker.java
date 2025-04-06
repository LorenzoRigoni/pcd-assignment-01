package pcd.ass01.virtualThreads;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.common.SimulationState;

/**
 * This class manages the updates on a single boid.
 */
public class BoidWorker extends Thread {
    private final Boid boid;
    private final BoidsModel model;
    private final UpdateBarrier barrier;
    private final SimulationState simulationState;
    private final WorkersCoordinator workersCoordinator;
    private boolean isRunning;

    public BoidWorker(Boid boid, BoidsModel model, UpdateBarrier barrier, SimulationState simulationState, WorkersCoordinator workersCoordinator) {
        this.boid = boid;
        this.model = model;
        this.barrier = barrier;
        this.simulationState = simulationState;
        this.workersCoordinator = workersCoordinator;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while(this.isRunning) {
            this.simulationState.waitForSimulation();

            this.boid.updateVelocity(this.model);

            this.barrier.waitBarrier(); //Wait all the boids to update their velocities

            this.boid.updatePos(this.model);

            this.workersCoordinator.workerDone();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.isRunning = false;
    }
}
