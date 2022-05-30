package ar.edu.itba.ss.pedestriandynamics.models;

import ar.edu.itba.ss.pedestriandynamics.utils.Constants;
import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

public class Zombie extends Pedestrian {
    private final double inactiveSpeed;
    private Vector2D wanderTarget;

    public Zombie(double initialX, double initialY, double desiredSpeed, double minRadius, double maxRadius, double beta, double tau, double inactiveSpeed) {
        super(initialX, initialY, desiredSpeed, minRadius, maxRadius, beta, tau);
        this.inactiveSpeed = inactiveSpeed;
    }

    public void setWanderTarget(Vector2D wanderTarget) {
        this.wanderTarget = wanderTarget;
    }

    public Vector2D getWanderTarget() {
        return wanderTarget;
    }

    public double getInactiveSpeed() {
        return inactiveSpeed;
    }

    public boolean isWandering() {
        return !this.currentPosition.isEqualVector(this.wanderTarget, Constants.EPSILON);
    }
}
