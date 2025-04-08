package pcd.ass01.virtualThreads;

import pcd.ass01.AbstractBoidsSimulator;
import pcd.ass01.BoidsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the virtual-threaded version of the simulator.
 */
public class VirtualThreadBoidsSimulator extends AbstractBoidsSimulator {
    private final BoidsModel model;
    private final List<Thread> workers;
    private WorkersCoordinator coordinator;
    private UpdateBarrier barrier;

    public VirtualThreadBoidsSimulator(BoidsModel model) {
        super();
        this.model = model;
        this.workers = new ArrayList<>();
    }

    @Override
    public void runSimulation() {
        while (true) {
            this.simulationState.waitForSimulation();

            if(this.workers.isEmpty())
                this.createVirtualThreads();

            var t0 = System.currentTimeMillis();
            var t0Nano = System.nanoTime();

            this.coordinator.waitWorkers();

            this.updateView(t0, t0Nano);

            if(this.simulationState.isStopped())
                this.interruptVirtualThreads();

            this.coordinator.coordinatorDone();
        }
    }

    private void createVirtualThreads() {
        this.coordinator = new WorkersCoordinator(this.model.getBoids().size());
        this.barrier = new UpdateBarrier(this.model.getBoids().size());
        this.model.getBoids().forEach(b ->
            this.workers.add(Thread.ofVirtual().start(new BoidWorker(b, this.model, this.barrier, this.simulationState, this.coordinator)))
        );
    }

    private void interruptVirtualThreads() {
        this.workers.forEach(Thread::interrupt);
        this.workers.clear();
    }
}