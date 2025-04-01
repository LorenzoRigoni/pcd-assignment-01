// MultithreadingBoidsSimulator.java
package pcd.ass01.task;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.BoidsSimulator;
import pcd.ass01.BoidsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskBoidsSimulator implements BoidsSimulator {
    private BoidsModel model;
    private Optional<BoidsView> view;
    private final ExecutorService executor;
    private final SimulationState simulationState;
    private final WorkersCoordinator coordinator;
    private static final int FRAMERATE = 25;
    private int framerate;

    public TaskBoidsSimulator(BoidsModel model) {
        this.model = model;
        this.view = Optional.empty();
        int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        this.executor = Executors.newFixedThreadPool(numThreads);
        this.simulationState = new SimulationState(true);
        this.coordinator = new WorkersCoordinator(numThreads);
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
        final List<Boid> boids = this.model.getBoids();
        final int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        final int boidsPerWorker = boids.size() / numThreads;

        while (true) {
            if (!simulationState.isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }

            long t0 = System.currentTimeMillis();

            // Phase 1: Update velocities
            List<Future<Void>> futures = new ArrayList<>();
            coordinator.reset(numThreads);

            int start, end;
            for (int i = 0; i < numThreads; i++) {
                start = i * boidsPerWorker;
                end = (i == numThreads - 1) ? boids.size() : start + boidsPerWorker;
                List<Boid> subList = boids.subList(start, end);
                futures.add(executor.submit(
                    new BoidWorker(subList, model, simulationState, coordinator, true)
                ));
            }

            try {
                coordinator.waitWorkers();
                coordinator.coordinatorDone();

                // Phase 2: Update positions
                coordinator.reset(numThreads);
                futures.clear();

                for (int i = 0; i < numThreads; i++) {
                    start = i * boidsPerWorker;
                    end = (i == numThreads - 1) ? boids.size() : start + boidsPerWorker;
                    List<Boid> subList = boids.subList(start, end);
                    futures.add(executor.submit(
                        new BoidWorker(subList, model, simulationState, coordinator, false)
                    ));
                }

                coordinator.waitWorkers();

                // Render
                if (view.isPresent()) {
                    view.get().update(framerate);
                    long t1 = System.currentTimeMillis();
                    long dtElapsed = t1 - t0;
                    System.out.println(dtElapsed);
                    long frameratePeriod = 1000/FRAMERATE;

                    if (dtElapsed < frameratePeriod) {
                        try {
                            Thread.sleep(frameratePeriod - dtElapsed);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        framerate = FRAMERATE;
                    } else {
                        framerate = (int) (1000/dtElapsed);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}