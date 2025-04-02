// MultithreadingBoidsSimulator.java
package pcd.ass01.task;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.BoidsSimulator;
import pcd.ass01.BoidsView;
import pcd.ass01.common.SimulationState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskBoidsSimulator implements BoidsSimulator {
    private BoidsModel model;
    private Optional<BoidsView> view;
    private final ExecutorService executor;
    private final SimulationState simulationState;
    private final UpdateLatch velocityLatch;
    private final UpdateLatch positionLatch;
    private final List<List<Boid>> boidLists;
    private static final int FRAMERATE = 25;
    private int framerate;

    public TaskBoidsSimulator(BoidsModel model) {
        this.model = model;
        this.view = Optional.empty();
        int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        this.executor = Executors.newFixedThreadPool(numThreads);
        this.simulationState = new SimulationState(true);
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
    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    @Override
    public boolean isRunning() {
        return this.simulationState.isRunning();
    }

    @Override
    public void resumeSimulation() {
        this.simulationState.resumeSimulation();
    }

    @Override
    public void suspendSimulation() {
        this.simulationState.suspendSimulation();
    }

    @Override
    public void runSimulation() {
        while(true) {
            this.simulationState.waitForSimulation();
            var t0 = System.currentTimeMillis();

            for (List<Boid> boids : this.boidLists) {
                this.executor.execute(new BoidTask(boids, this.model, this.simulationState, this.velocityLatch, this.positionLatch));
            }

            if (view.isPresent()) {
                view.get().update(framerate);
                var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                System.out.println(dtElapsed);
                var framratePeriod = 1000/FRAMERATE;
    
                if (dtElapsed < framratePeriod) {
                    try {
                        Thread.sleep(framratePeriod - dtElapsed);
                    } catch (Exception ex) {}
                    framerate = FRAMERATE;
                } else {
                    framerate = (int) (1000/dtElapsed);
                }
            }
        }
    }
}