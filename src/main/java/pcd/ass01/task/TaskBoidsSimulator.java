package pcd.ass01.task;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskBoidsSimulator extends AbstractBoidsSimulator {
    private final BoidsModel model;
    private ExecutorService executor;
    private UpdateLatch velocityLatch;
    private UpdateLatch positionLatch;
    private List<List<Boid>> boidLists;
    private final int numThreads = Runtime.getRuntime().availableProcessors() + 1;
    private boolean isRunning;

    public TaskBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        this.executor = Executors.newFixedThreadPool(numThreads);
        this.boidLists = new ArrayList<>();
        this.isRunning = true;
    }

    @Override
    public void runSimulation() {
        while(this.isRunning) {
            this.simulationState.waitForSimulation();

            if (this.boidLists.isEmpty())
                this.createTasks();

            var t0 = System.currentTimeMillis();

            if(!this.isRunning)
                break;

            for (List<Boid> boids : this.boidLists) {
                this.executor.execute(new BoidTask(boids, this.model, this.simulationState, this.velocityLatch, this.positionLatch));
            }

            this.updateView(t0);
        }
    }

    @Override
    public void startSimulation() {
        super.startSimulation();
        this.isRunning = true;
        this.executor = Executors.newFixedThreadPool(this.numThreads);
        new Thread(this::runSimulation).start();
    }

    @Override
    public void stopSimulation() {
        super.stopSimulation();
        this.isRunning = false;
        this.executor.shutdown();
        this.boidLists.clear();
    }

    private void createTasks() {
        final List<Boid> boids = this.model.getBoids();
        final int boidsPerWorker = boids.size() / numThreads;

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
}