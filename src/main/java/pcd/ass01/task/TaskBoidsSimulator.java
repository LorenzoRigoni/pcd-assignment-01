package pcd.ass01.task;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static pcd.ass01.utilities.Costants.NUM_THREADS;
import static pcd.ass01.utilities.Costants.TASK_POOL_SIZE;

/**
 * This class is the task-based version of the simulator.
 */
public class TaskBoidsSimulator extends AbstractBoidsSimulator {
    private final List<List<Boid>> boidsLists;
    private final List<Future<Void>> futures;
    private ExecutorService executor;
    private final BoidsModel model;
    private boolean isRunning;

    public TaskBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
        this.boidsLists = new ArrayList<>();
        this.futures = new ArrayList<>();
        this.isRunning = true;
    }

    @Override
    public void runSimulation() {
        while(this.isRunning) {
            this.simulationState.waitForSimulation();

            if (this.boidsLists.isEmpty())
                this.createBoidsLists();

            var t0 = System.currentTimeMillis();

            for (List<Boid> boids : this.boidsLists) {
                this.futures.add(this.executor.submit(new VelocityBoidTask(boids, this.model, this.simulationState)));
            }

            this.waitFutures();
            this.futures.clear();

            for (List<Boid> boids : this.boidsLists) {
                this.futures.add(this.executor.submit(new PositionBoidTask(boids, this.model, this.simulationState)));
            }

            this.waitFutures();
            this.futures.clear();

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
        this.boidsLists.clear();
    }

    private void createBoidsLists() {
        final List<Boid> boids = this.model.getBoids();
        final int boidsPerWorker = boids.size() / TASK_POOL_SIZE;
        final int numTasks = Math.max(1, boids.size() / TASK_POOL_SIZE);

        int start;
        int end;
        for (int i = 0; i < numTasks; i++) {
            start = i * boidsPerWorker;
            end = (i == numTasks - 1) ? boids.size() : start + boidsPerWorker;
            this.boidsLists.add(boids.subList(start, end));
        }
    }

    private void waitFutures() {
        this.futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}