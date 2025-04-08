package pcd.ass01.task;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.common.SimulationState;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class represents the task of the update of the velocity.
 */
public class VelocityBoidTask implements Callable<Void> {
    private final List<Boid> boids;
    private final BoidsModel model;
    private final SimulationState simulationState;

    public VelocityBoidTask(List<Boid> boids, BoidsModel model, SimulationState simulationState) {
        this.boids = boids;
        this.model = model;
        this.simulationState = simulationState;
    }

    @Override
    public Void call() {
        this.simulationState.waitForSimulation();
        
        for(Boid boid : this.boids)
            boid.updateVelocity(this.model);

        return null;
    }
}