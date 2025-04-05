package pcd.ass01.multithreading;

import pcd.ass01.*;

import java.util.ArrayList;
import java.util.List;

public class MultithreadingBoidsSimulator extends AbstractBoidsSimulator {
    private WorkersCoordinator coordinator;
    private final List<BoidWorker> workers;
    private final int numThreads = Runtime.getRuntime().availableProcessors() + 1;
    private final BoidsModel model;

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
            this.coordinator.waitWorkers();

            this.updateView(t0);

            if (this.simulationState.isStopped())
                this.stopWorkers();

            this.coordinator.coordinatorDone();
        }
    }

    private void stopWorkers() {
        this.workers.forEach(BoidWorker::interrupt);
        this.workers.clear();
    }

    private void createWorkers() {
        final List<Boid> boids = model.getBoids();
        final int boidsPerWorker = boids.size() / numThreads;
        final UpdateBarrier barrier = new UpdateBarrier(numThreads);
        this.coordinator = new WorkersCoordinator(numThreads);

        int start;
        int end;
        List<Boid> subList;
        for (int i = 0; i < numThreads; i++) {
            start = i * boidsPerWorker;
            end = (i == numThreads - 1) ? boids.size() : start + boidsPerWorker;
            subList = boids.subList(start, end);
            workers.add(new BoidWorker(subList, model, barrier, this.simulationState, this.coordinator));
            workers.get(i).start();
        }
    }
}
