package pcd.ass01.multithreading.jpf;

import pcd.ass01.multithreading.UpdateBarrier;
import pcd.ass01.multithreading.WorkersCoordinator;

import java.util.List;

public class JPFBoidWorker extends Thread {
    private final List<JPFBoid> boids;
    private final JPFBoidsModel model;
    private final UpdateBarrier barrier;
    private final WorkersCoordinator workersCoordinator;
    private static final int NUM_ITERATIONS = 1;

    public JPFBoidWorker(List<JPFBoid> boids, JPFBoidsModel model, UpdateBarrier barrier, WorkersCoordinator workersCoordinator) {
        this.boids = boids;
        this.model = model;
        this.barrier = barrier;
        this.workersCoordinator = workersCoordinator;
    }

    @Override
    public void run() {
        int count = 0;
        while(count < NUM_ITERATIONS) {

            for (JPFBoid boid : this.boids) {
                boid.updateVelocity(model);
            }

            this.barrier.waitBarrier(); //Wait all the boids to update their velocities

            for (JPFBoid boid : this.boids) {
                boid.updatePos(model);
            }

            this.workersCoordinator.workerDone();
            count++;
        }
    }
}
