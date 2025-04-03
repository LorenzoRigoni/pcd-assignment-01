package pcd.ass01.multithreading.jpf;

public class JPFBoidsSimulation {

    public static void main(String[] args) {
        var sim = new JPFBoidsSimulator();
        sim.runSimulation();
        sim.stopSimulation();
    }
}
