package pcd.ass01;

import pcd.ass01.multithreading.MultithreadingBoidsSimulator;
import pcd.ass01.task.TaskBoidsSimulator;
import pcd.ass01.virtualThreads.VirtualThreadBoidsSimulator;

import static pcd.ass01.utilities.Costants.*;

public class BoidsSimulation {

    public static void main(String[] args) {
    	var model = new BoidsModel(
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
