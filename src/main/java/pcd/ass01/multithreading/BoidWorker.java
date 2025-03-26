package pcd.ass01.multithreading;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BoidWorker extends Thread {
    private final List<Boid> boids;
    private final BoidsModel model;
    private final CyclicBarrier barrier;
    private final SimulationState simulationState;
    private final WorkersCoordinator workersCoordinator;

    public BoidWorker(List<Boid> boids, BoidsModel model, CyclicBarrier barrier, SimulationState simulationState, WorkersCoordinator workersCoordinator) {
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

            for (Boid boid : boids) {
                boid.updateVelocity(model);
            }

            try {
                barrier.await(); //Wait all the boids to update their velocities
            } catch (InterruptedException | BrokenBarrierException e) {}

            for (Boid boid : boids) {
                boid.updatePos(model);
            }

            this.workersCoordinator.workerDone();
        }
    }
}
