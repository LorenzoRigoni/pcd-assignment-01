package pcd.ass01.multithreading;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * This class represents the thread that manage a subset of boids.
 * It performs the update of velocities and positions of the boids.
 */
public class BoidWorker extends Thread {
    private final List<Boid> boids;
    private final BoidsModel model;
    private final UpdateBarrier barrier;
    private final SimulationState simulationState;
    private final WorkersCoordinator workersCoordinator;

    public BoidWorker(List<Boid> boids, BoidsModel model, UpdateBarrier barrier, SimulationState simulationState, WorkersCoordinator workersCoordinator) {
        this.boids = boids;
        this.model = model;
        this.barrier = barrier;
        this.simulationState = simulationState;
        this.workersCoordinator = workersCoordinator;
    }

    @Override
    public void run() {
        while(true) {
            this.simulationState.waitForSimulation();

            for (Boid boid : this.boids) {
                boid.updateVelocity(model);
            }

            this.barrier.waitBarrier(); //Wait all the boids to update their velocities

            for (Boid boid : this.boids) {
                boid.updatePos(model);
            }

            this.workersCoordinator.workerDone();
        }
    }
}
