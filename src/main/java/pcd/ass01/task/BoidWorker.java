// BoidWorker.java
package pcd.ass01.task;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class represents the task that manages a subset of boids.
 */
public class BoidWorker implements Callable<Void> {
    private final List<Boid> boids;
    private final BoidsModel model;
    private final SimulationState simulationState;
    private final WorkersCoordinator workersCoordinator;
    private final boolean firstPhase;

    public BoidWorker(List<Boid> boids, BoidsModel model, 
                     SimulationState simulationState, 
                     WorkersCoordinator workersCoordinator,
                     boolean firstPhase) {
        this.boids = boids;
        this.model = model;
        this.simulationState = simulationState;
        this.workersCoordinator = workersCoordinator;
        this.firstPhase = firstPhase;
    }

    @Override
    public Void call() throws Exception {
        if (!simulationState.isRunning()) {
            return null;
        }

        if (firstPhase) {
            // Update velocities phase
            for (Boid boid : this.boids) {
                boid.updateVelocity(model);
            }
            workersCoordinator.workerDone();
            workersCoordinator.waitForRender();
        } else {
            // Update positions phase
            for (Boid boid : this.boids) {
                boid.updatePos(model);
            }
            workersCoordinator.workerDone();
        }
        return null;
    }
}