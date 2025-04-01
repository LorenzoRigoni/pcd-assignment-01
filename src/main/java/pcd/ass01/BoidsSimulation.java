package pcd.ass01;

import pcd.ass01.multithreading.MultithreadingBoidsSimulator;
import pcd.ass01.task.TaskBoidsSimulator;
import pcd.ass01.virtualThreads.VirtualThreadBoidsSimulator;

import javax.swing.*;

import static pcd.ass01.utilities.Costants.*;

public class BoidsSimulation {

    public static void main(String[] args) {
		String numberOfBoids;

		do {
			numberOfBoids = (String) JOptionPane.showInputDialog(
					null,
					"Enter the number of boids (it must be a positive integer):",
					"Number of Boids",
					JOptionPane.QUESTION_MESSAGE,
					null,
					null,
					1500);
		} while (numberOfBoids.isEmpty() || Integer.parseInt(numberOfBoids) <= 0);

    	var model = new BoidsModel(
    					Integer.parseInt(numberOfBoids),
    					SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT, 
    					ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
    					MAX_SPEED,
    					PERCEPTION_RADIUS,
    					AVOID_RADIUS); 
    	//var sim = new MultithreadingBoidsSimulator(model);
		var sim = new TaskBoidsSimulator(model);
		//var sim = new VirtualThreadBoidsSimulator(model);
    	var view = new BoidsView(model, sim, SCREEN_WIDTH, SCREEN_HEIGHT);
    	sim.attachView(view);
    	sim.runSimulation();
    }
}
