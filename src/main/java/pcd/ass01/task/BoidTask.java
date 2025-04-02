// BoidWorker.java
package pcd.ass01.task;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.common.SimulationState;
import java.util.List;

/**
 * This class represents the task that manages a subset of boids.
 */
public class BoidTask implements Runnable {
    private final List<Boid> boids;
    private final BoidsModel model;
    private final SimulationState simulationState;
    private final UpdateLatch velocityLatch;
    private final UpdateLatch positionLatch;

    public BoidTask(List<Boid> boids, BoidsModel model,
                    SimulationState simulationState,
                    UpdateLatch velocityLatch, UpdateLatch positionLatch) {
        this.boids = boids;
        this.model = model;
        this.simulationState = simulationState;
        this.velocityLatch = velocityLatch;
        this.positionLatch = positionLatch;
    }

    @Override
    public void run() {
        this.simulationState.waitForSimulation();
        
        for(Boid boid : this.boids) {
            boid.updateVelocity(model);
        }

        this.velocityLatch.countDown();
        this.velocityLatch.await();

        for(Boid boid : this.boids) {
            boid.updatePos(model);
        }

        this.positionLatch.countDown();
        this.positionLatch.await();
    }
}