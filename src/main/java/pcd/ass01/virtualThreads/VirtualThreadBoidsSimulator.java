package pcd.ass01.virtualThreads;

import pcd.ass01.AbstractBoidsSimulator;
import pcd.ass01.BoidsModel;

public class VirtualThreadBoidsSimulator extends AbstractBoidsSimulator {
    private final WorkersCoordinator coordinator;
    private final UpdateBarrier barrier;

    public VirtualThreadBoidsSimulator(BoidsModel model) {
        super();
        this.coordinator = new WorkersCoordinator(model.getBoids().size());
        this.barrier = new UpdateBarrier(model.getBoids().size());

        //Creates a virtual thread for each boid
        model.getBoids().forEach(boid -> {
            Thread.ofVirtual().start(() -> {
                while (true) {
                    simulationState.waitForSimulation();

                    boid.updateVelocity(model);
                    //System.out.println("Velocità aggiornata per il boid: " + boid);

                    this.barrier.waitBarrier();

                    boid.updatePos(model);
                    //System.out.println("Posizione aggiornata per il boid: " + boid);
                    coordinator.workerDone();

                }
            });
        });
    }

    @Override
    public void runSimulation() {
        while (true) {
            this.simulationState.waitForSimulation();
            var t0 = System.currentTimeMillis();

            this.coordinator.waitWorkers();

            this.updateView(t0);

            this.coordinator.coordinatorDone();
        }
    }
}