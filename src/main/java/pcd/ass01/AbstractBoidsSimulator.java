package pcd.ass01;

import pcd.ass01.common.SimulationState;

import java.util.Optional;

public abstract class AbstractBoidsSimulator implements BoidsSimulator {
    protected final SimulationState simulationState;
    protected Optional<BoidsView> view;
    protected int framerate;
    protected static final int FRAMERATE = 25;

    public AbstractBoidsSimulator() {
        this.view = Optional.empty();
        this.simulationState = new SimulationState(true);
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
    public abstract void runSimulation();

    protected void updateView(long t0) {
        if (view.isPresent()) {
            view.get().update(framerate);
            var t1 = System.currentTimeMillis();
            var dtElapsed = t1 - t0;
            System.out.println(dtElapsed);
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
    }
}
