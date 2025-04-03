package pcd.ass01.multithreading.jpf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class JPFBoid {
    private P2d pos;
    private V2d vel;
    private final ReentrantLock lock;

    public JPFBoid(P2d pos, V2d vel, ReentrantLock lock) {
        this.pos = pos;
        this.vel = vel;
        this.lock = lock;
    }

    public P2d getPos() {
        return pos;
    }

    public V2d getVel() {
        return vel;
    }

    public void updateVelocity(JPFBoidsModel model) {
        /* change velocity vector according to separation, alignment, cohesion */

        List<JPFBoid> nearbyBoids = getNearbyBoids(model);

        this.lock.lock();
        try {
            V2d separation = calculateSeparation(nearbyBoids, model);
            V2d alignment = calculateAlignment(nearbyBoids, model);
            V2d cohesion = calculateCohesion(nearbyBoids, model);
            vel = vel.sum(alignment.mul(model.getAlignmentWeight()))
                    .sum(separation.mul(model.getSeparationWeight()))
                    .sum(cohesion.mul(model.getCohesionWeight()));

            /* Limit speed to MAX_SPEED */

            double speed = vel.abs();

            if (speed > model.getMaxSpeed()) {
                vel = vel.getNormalized().mul(model.getMaxSpeed());
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void updatePos(JPFBoidsModel model) {
        /* Update position */

        pos = pos.sum(vel);

        /* environment wrap-around */

        if (pos.x() < model.getMinX()) pos = pos.sum(new V2d(model.getWidth(), 0));
        if (pos.x() >= model.getMaxX()) pos = pos.sum(new V2d(-model.getWidth(), 0));
        if (pos.y() < model.getMinY()) pos = pos.sum(new V2d(0, model.getHeight()));
        if (pos.y() >= model.getMaxY()) pos = pos.sum(new V2d(0, -model.getHeight()));
    }

    private List<JPFBoid> getNearbyBoids(JPFBoidsModel model) {
        var list = new ArrayList<JPFBoid>();
        for (JPFBoid other : model.getBoids()) {
            if (other != this) {
                P2d otherPos = other.getPos();
                double distance = pos.distance(otherPos);
                if (distance < model.getPerceptionRadius()) {
                    list.add(other);
                }
            }
        }
        return list;
    }

    private V2d calculateAlignment(List<JPFBoid> nearbyBoids, JPFBoidsModel model) {
        double avgVx = 0;
        double avgVy = 0;
        if (nearbyBoids.size() > 0) {
            for (JPFBoid other : nearbyBoids) {
                V2d otherVel = other.getVel();
                avgVx += otherVel.x();
                avgVy += otherVel.y();
            }
            avgVx /= nearbyBoids.size();
            avgVy /= nearbyBoids.size();
            return new V2d(avgVx - vel.x(), avgVy - vel.y()).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }

    private V2d calculateCohesion(List<JPFBoid> nearbyBoids, JPFBoidsModel model) {
        double centerX = 0;
        double centerY = 0;
        if (nearbyBoids.size() > 0) {
            for (JPFBoid other: nearbyBoids) {
                P2d otherPos = other.getPos();
                centerX += otherPos.x();
                centerY += otherPos.y();
            }
            centerX /= nearbyBoids.size();
            centerY /= nearbyBoids.size();
            return new V2d(centerX - pos.x(), centerY - pos.y()).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }

    private V2d calculateSeparation(List<JPFBoid> nearbyBoids, JPFBoidsModel model) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (JPFBoid other: nearbyBoids) {
            P2d otherPos = other.getPos();
            double distance = pos.distance(otherPos);
            if (distance < model.getAvoidRadius()) {
                dx += pos.x() - otherPos.x();
                dy += pos.y() - otherPos.y();
                count++;
            }
        }
        if (count > 0) {
            dx /= count;
            dy /= count;
            return new V2d(dx, dy).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }
}
