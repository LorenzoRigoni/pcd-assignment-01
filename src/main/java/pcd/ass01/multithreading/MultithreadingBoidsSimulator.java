package pcd.ass01.multithreading;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.BoidsSimulator;
import pcd.ass01.BoidsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CyclicBarrier;

public class MultithreadingBoidsSimulator extends BoidsSimulator {
    private BoidsModel model;
    private final List<BoidWorker> workers;
    private final CyclicBarrier barrier;
    private final SimulationState simulationState;
    private final WorkersCoordinator coordinator;

    private static final int FRAMERATE = 25;
    private int framerate;

    public MultithreadingBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        this.workers = new ArrayList<>();

        final int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        final List<Boid> boids = this.model.getBoids();
        final int boidsPerWorker = boids.size() / numThreads;

        this.barrier = new CyclicBarrier(numThreads);
        this.simulationState = new SimulationState(true);
        this.coordinator = new WorkersCoordinator(numThreads);

        int start;
        int end;
        List<Boid> subList;
        for (int i = 0; i < numThreads; i++) {
            start = i * boidsPerWorker;
            end = (i == numThreads - 1) ? boids.size() : start + boidsPerWorker;
            subList = boids.subList(start, end);
            this.workers.add(new BoidWorker(subList, model, this.barrier, this.simulationState, this.coordinator));
            this.workers.get(i).start();
        }
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

    public void runSimulation() {
        while (true) {
            this.simulationState.waitForSimulation();
            var t0 = System.currentTimeMillis();
            this.coordinator.waitWorkers();

            if (view.isPresent()) {
                view.get().update(framerate);
                var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
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
            this.coordinator.coordinatorDone();
        }
    }
}
