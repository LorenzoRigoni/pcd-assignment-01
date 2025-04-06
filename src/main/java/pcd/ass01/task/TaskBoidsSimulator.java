package pcd.ass01.task;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pcd.ass01.utilities.Costants.NUM_THREADS;

/**
 * This class is the task-based version of the simulator.
 */
public class TaskBoidsSimulator extends AbstractBoidsSimulator {
    private final BoidsModel model;
    private ExecutorService executor;
    private UpdateLatch velocityLatch;
    private UpdateLatch positionLatch;
    private List<List<Boid>> boidLists;
    private boolean isRunning;

    public TaskBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
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
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
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
        final int boidsPerWorker = boids.size() / NUM_THREADS;

        int start;
        int end;
        for (int i = 0; i < NUM_THREADS; i++) {
            start = i * boidsPerWorker;
            end = (i == NUM_THREADS - 1) ? boids.size() : start + boidsPerWorker;
            this.boidLists.add(boids.subList(start, end));
        }

        this.velocityLatch = new UpdateLatch(this.boidLists.size());
        this.positionLatch = new UpdateLatch(this.boidLists.size());
    }
}