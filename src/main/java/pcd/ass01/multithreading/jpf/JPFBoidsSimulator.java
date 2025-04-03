package pcd.ass01.multithreading.jpf;

import pcd.ass01.multithreading.UpdateBarrier;
import pcd.ass01.multithreading.WorkersCoordinator;

import static pcd.ass01.utilities.Costants.*;

import java.util.LinkedList;
import java.util.List;

public class JPFBoidsSimulator {
    private final WorkersCoordinator coordinator;
    private final List<JPFBoidWorker> workers;
    private static final int NUM_ITERATIONS = 1;

    public JPFBoidsSimulator() {
        int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        JPFBoidsModel model = new JPFBoidsModel(
                10,
                SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
                ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                MAX_SPEED,
                PERCEPTION_RADIUS,
                AVOID_RADIUS
        );
        this.workers = new LinkedList<>();
        this.coordinator = new WorkersCoordinator(numThreads);
        final UpdateBarrier barrier = new UpdateBarrier(numThreads);

        List<JPFBoid> subList;
        int start, end;
        final List<JPFBoid> boids = model.getBoids();
        final int boidsPerWorker = boids.size() / numThreads;
        for (int i = 0; i < numThreads; i++) {
            start = i * boidsPerWorker;
            end = (i == numThreads - 1) ? boids.size() : start + boidsPerWorker;
            subList = boids.subList(start, end);
            workers.add(new JPFBoidWorker(subList, model, barrier, this.coordinator));
            workers.get(i).start();
        }
    }

    public void runSimulation() {
        int count = 0;
        while(count < NUM_ITERATIONS) {
            this.coordinator.waitWorkers();
            this.coordinator.coordinatorDone();
            count++;
        }
    }

    public synchronized void stopSimulation() {
        for (JPFBoidWorker worker : this.workers) {
            worker.interrupt();
        }
    }
}
