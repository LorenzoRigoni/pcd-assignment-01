package pcd.ass01.task;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskBoidsSimulator extends AbstractBoidsSimulator {
    private final BoidsModel model;
    private final ExecutorService executor;
    private final UpdateLatch velocityLatch;
    private final UpdateLatch positionLatch;
    private final List<List<Boid>> boidLists;

    public TaskBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        this.executor = Executors.newFixedThreadPool(numThreads);
        final List<Boid> boids = this.model.getBoids();
        final int boidsPerWorker = boids.size() / numThreads;
        this.boidLists = new ArrayList<>();

        int start;
        int end;
        for (int i = 0; i < numThreads; i++) {
            start = i * boidsPerWorker;
            end = (i == numThreads - 1) ? boids.size() : start + boidsPerWorker;
            this.boidLists.add(boids.subList(start, end));
        }
        this.velocityLatch = new UpdateLatch(this.boidLists.size());
        this.positionLatch = new UpdateLatch(this.boidLists.size());
    }

    @Override
    public void runSimulation() {
        while(true) {
            this.simulationState.waitForSimulation();
            var t0 = System.currentTimeMillis();

            for (List<Boid> boids : this.boidLists) {
                this.executor.execute(new BoidTask(boids, this.model, this.simulationState, this.velocityLatch, this.positionLatch));
            }

            this.updateView(t0);
        }
    }
}