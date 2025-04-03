package pcd.ass01.multithreading.jpf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class JPFBoidsModel {
    private final List<JPFBoid> boids;
    private double separationWeight;
    private double alignmentWeight;
    private double cohesionWeight;
    private final double width;
    private final double height;
    private final double maxSpeed;
    private final double perceptionRadius;
    private final double avoidRadius;
    private final ReentrantLock lock;

    public JPFBoidsModel(int nboids,
                         double initialSeparationWeight,
                         double initialAlignmentWeight,
                         double initialCohesionWeight,
                         double width,
                         double height,
                         double maxSpeed,
                         double perceptionRadius,
                         double avoidRadius){
        separationWeight = initialSeparationWeight;
        alignmentWeight = initialAlignmentWeight;
        cohesionWeight = initialCohesionWeight;
        this.width = width;
        this.height = height;
        this.maxSpeed = maxSpeed;
        this.perceptionRadius = perceptionRadius;
        this.avoidRadius = avoidRadius;

        final DoubleGenerator generator = new DoubleGenerator();
        this.lock = new ReentrantLock();

        boids = new ArrayList<>();
        for (int i = 0; i < nboids; i++) {
            P2d pos = new P2d(-width/2 + generator.next() * width, -height/2 + generator.next() * height);
            V2d vel = new V2d(generator.next() * maxSpeed/2 - maxSpeed/4, generator.next() * maxSpeed/2 - maxSpeed/4);
            boids.add(new JPFBoid(pos, vel, this.lock));
        }

    }

    public synchronized List<JPFBoid> getBoids(){
        return boids;
    }

    public double getMinX() {
        return -width/2;
    }

    public double getMaxX() {
        return width/2;
    }

    public double getMinY() {
        return -height/2;
    }

    public double getMaxY() {
        return height/2;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public synchronized void setSeparationWeight(double value) {
        this.separationWeight = value;
    }

    public synchronized void setAlignmentWeight(double value) {
        this.alignmentWeight = value;
    }

    public synchronized void setCohesionWeight(double value) {
        this.cohesionWeight = value;
    }

    public synchronized double getSeparationWeight() {
        return separationWeight;
    }

    public synchronized double getCohesionWeight() {
        return cohesionWeight;
    }

    public synchronized double getAlignmentWeight() {
        return alignmentWeight;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvoidRadius() {
        return avoidRadius;
    }

    public double getPerceptionRadius() {
        return perceptionRadius;
    }
}
