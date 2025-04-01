// BoidWorker.java
package pcd.ass01.task;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import java.util.List;

/**
 * This class represents the task that manages a subset of boids.
 */
public class BoidWorker implements Runnable{
    private final List<Boid> boids;
    private final BoidsModel model;
    private final WorkersCoordinator workersCoordinator;
    private final SimulationState simulationState;

    public BoidWorker(List<Boid> boids, BoidsModel model, 
                     SimulationState simulationState, 
                     WorkersCoordinator workersCoordinator) {
        this.boids = boids;
        this.model = model;
        this.workersCoordinator = workersCoordinator;
        this.simulationState = simulationState;
    }

    @Override
    public void run() {
        this.simulationState.waitForSimulation();
        
        for(Boid boid : this.boids) {
            boid.updatePos(model);
        }
        workersCoordinator.workerDone();
        
        try {
            workersCoordinator.waitWorkers();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            return;
        }
        
        for(Boid boid : this.boids) {
            boid.updateVelocity(model);
        }
        workersCoordinator.workerDone();
    }
}