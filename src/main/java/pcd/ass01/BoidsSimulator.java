package pcd.ass01;

import java.util.Optional;

public interface BoidsSimulator {
    /**
     * Attaches the view to the simulator.
     *
     * @param view The view to attach
     */
    void attachView(BoidsView view);

    /**
     * Checks if the simulator is running.
     *
     * @return true if it is running, false otherwise
     */
    boolean isRunning();

    /**
     * Resumes the simulation.
     */
    void resumeSimulation();

    /**
     * Suspends the simulation.
     */
    void suspendSimulation();

    /**
     * Runs the simulation.
     */
    void runSimulation();
}
