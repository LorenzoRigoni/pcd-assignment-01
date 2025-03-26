package pcd.ass01;

import javax.swing.*;

import static pcd.ass01.utilities.Costants.*;

public class BoidsSimulation {

    public static void main(String[] args) {
		String numberOfBoids = "";

		do {
			numberOfBoids = JOptionPane.showInputDialog("Enter the number of boids (it must be a positive integer):",
					JOptionPane.QUESTION_MESSAGE);
		} while (numberOfBoids.isEmpty() || Integer.parseInt(numberOfBoids) <= 0);

    	var model = new BoidsModel(
    					Integer.parseInt(numberOfBoids),
    					SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT, 
    					ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
    					MAX_SPEED,
    					PERCEPTION_RADIUS,
    					AVOID_RADIUS); 
    	var sim = new BoidsSimulator(model);
    	var view = new BoidsView(model, sim, SCREEN_WIDTH, SCREEN_HEIGHT);
    	sim.attachView(view);
    	sim.runSimulation();
    }
}
