package pcd.ass01.task;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static pcd.ass01.utilities.Costants.TASK_POOL_SIZE;

/**
 * This class is the task-based version of the simulator.
 */
public class TaskBoidsSimulator extends AbstractBoidsSimulator {
    private final List<List<Boid>> boidsLists;
    private final List<Future<Void>> futures;
    private final BoidsModel model;
    private ExecutorService executor;
    private boolean isRunning;

    public TaskBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        this.executor = Executors.newCachedThreadPool();
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

            this.boidsLists.forEach(b ->
                this.futures.add(this.executor.submit(new VelocityBoidTask(b, this.model, this.simulationState)))
            );

            this.waitFuturesAndClear();

            this.boidsLists.forEach(b ->
                this.futures.add(this.executor.submit(new PositionBoidTask(b, this.model, this.simulationState)))
            );

            this.waitFuturesAndClear();

            this.updateView(t0);
        }
    }

    @Override
    public void startSimulation() {
        super.startSimulation();
        this.isRunning = true;
        this.executor = Executors.newCachedThreadPool();
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
        final int boidsPerTask = boids.size() / TASK_POOL_SIZE;
        final int numTasks = Math.max(1, boidsPerTask);

        int start;
        int end;
        for (int i = 0; i < numTasks; i++) {
            start = i * boidsPerTask;
            end = (i == numTasks - 1) ? boids.size() : start + boidsPerTask;
            this.boidsLists.add(boids.subList(start, end));
        }
    }

    private void waitFuturesAndClear() {
        this.futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        this.futures.clear();
    }
}