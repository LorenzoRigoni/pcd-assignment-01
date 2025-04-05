package pcd.ass01;


public interface BoidsSimulator {
    /**
     * Attaches the view to the simulator.
     *
     * @param view The view to attach
     */
    void attachView(BoidsView view);

    /**
     * Checks if the simulator is paused.
     *
     * @return true if it is paused, false otherwise
     */
    boolean isPaused();

    /**
     * Checks if the simulator is stopped.
     *
     * @return true if it is stopped, false otherwise
     */
    boolean isStopped();

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

    /**
     * Starts the simulation.
     */
    void startSimulation();

    /**
     * Stops the simulation.
     */
    void stopSimulation();
}
