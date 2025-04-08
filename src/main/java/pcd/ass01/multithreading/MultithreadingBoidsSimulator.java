package pcd.ass01.multithreading;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;

import static pcd.ass01.utilities.Costants.NUM_THREADS;

/**
 * This class is the multithreaded version of the simulator.
 */
public class MultithreadingBoidsSimulator extends AbstractBoidsSimulator {
    private final List<BoidWorker> workers;
    private final BoidsModel model;
    private WorkersCoordinator coordinator;

    public MultithreadingBoidsSimulator(BoidsModel model) {
        super();
        this.workers = new ArrayList<>();
        this.model = model;
    }

    @Override
    public void runSimulation() {
        while (true) {
            this.simulationState.waitForSimulation();

            if (this.workers.isEmpty())
                this.createWorkers();

            var t0 = System.currentTimeMillis();
            var t0Nano = System.nanoTime();
            this.coordinator.waitWorkers();

            this.updateView(t0, t0Nano);

            if (this.simulationState.isStopped())
                this.stopWorkers();

            this.coordinator.coordinatorDone();
        }
    }

    private void createWorkers() {
        final List<Boid> boids = model.getBoids();
        final int boidsPerWorker = boids.size() / NUM_THREADS;
        final UpdateBarrier barrier = new UpdateBarrier(NUM_THREADS);
        this.coordinator = new WorkersCoordinator(NUM_THREADS);

        int start;
        int end;
        List<Boid> subList;
        for (int i = 0; i < NUM_THREADS; i++) {
            start = i * boidsPerWorker;
            end = (i == NUM_THREADS - 1) ? boids.size() : start + boidsPerWorker;
            subList = boids.subList(start, end);
            workers.add(new BoidWorker(subList, model, barrier, this.simulationState, this.coordinator));
            workers.get(i).start();
        }
    }

    private void stopWorkers() {
        this.workers.forEach(BoidWorker::interrupt);
        this.workers.clear();
    }
}
