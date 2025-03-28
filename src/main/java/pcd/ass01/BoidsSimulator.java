package pcd.ass01;

import java.util.Optional;

public class BoidsSimulator {
    protected Optional<BoidsView> view;

    public BoidsSimulator() {
        this.view = Optional.empty();
    }

    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    public boolean isRunning() {
        return true;
    }

    public void resumeSimulation() {}

    public void suspendSimulation() {}
}
