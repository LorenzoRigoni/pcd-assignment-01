package pcd.ass01.virtualThreads;

import pcd.ass01.BoidsModel;
import pcd.ass01.BoidsSimulator;
import pcd.ass01.BoidsView;

import java.util.Optional;

public class VirtualThreadBoidsSimulator implements BoidsSimulator {
    private final BoidsModel model;
    private Optional<BoidsView> view = Optional.empty();
    private final SimulationState simulationState;
    private final WorkersCoordinator coordinator;
    private final UpdateBarrier barrier;
    private static final int FRAMERATE = 25;
    private int framerate;

    public VirtualThreadBoidsSimulator(BoidsModel model) {
        this.model = model;
        this.simulationState = new SimulationState(true);
        this.coordinator = new WorkersCoordinator(model.getBoids().size());
        this.barrier = new UpdateBarrier(model.getBoids().size());

        //Creates a virtual thread for each boid
        model.getBoids().forEach(boid -> {
            Thread.ofVirtual().start(() -> {
                while (true) {
                    simulationState.waitForSimulation();

                    boid.updateVelocity(model);
                    System.out.println("Velocità aggiornata per il boid: " + boid);

                    this.barrier.waitBarrier();

                    boid.updatePos(model);
                    System.out.println("Posizione aggiornata per il boid: " + boid);
                    coordinator.workerDone();

                }
            });
        });
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

    public void runSimulation() {
        while (true) {
            simulationState.waitForSimulation();
            var t0 = System.currentTimeMillis();

            coordinator.waitWorkers();

            view.ifPresent(v -> {
                v.update(framerate);
                var t1 = System.currentTimeMillis();
                var dtElapsed = t1 - t0;
                var framePeriod = 1000 / FRAMERATE;

                if (dtElapsed < framePeriod) {
                    try {
                        Thread.sleep(framePeriod - dtElapsed);
                    } catch (InterruptedException ignored) {}
                    framerate = FRAMERATE;
                } else {
                    framerate = (int) (1000 / dtElapsed);
                }
            });
            coordinator.coordinatorDone();
        }
    }

}

