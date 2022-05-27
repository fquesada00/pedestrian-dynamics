package ar.edu.itba.ss.pedestriandynamics.models;

import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

public abstract class Pedestrian {

    private Vector2D currentPosition;
    private Vector2D currentVelocity;
    private Vector2D nextPosition;
    private Vector2D nextVelocity;
    private final double desiredSpeed;
    private final double maxRadius;
    private final double minRadius;
    private double currentRadius;
    private final double beta;
    private final double tau;

    public Pedestrian(double initialX, double initialY, double desiredSpeed, double minRadius, double maxRadius, double beta, double tau) {
        this.desiredSpeed = desiredSpeed;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.beta = beta;
        this.tau = tau;
        this.currentPosition = new Vector2D(initialX, initialY);
    }

    public void move() {
        this.currentPosition = this.nextPosition;
    }

    public void contract() {
        this.currentRadius = this.minRadius;
    }

    public boolean overlaps(Pedestrian pedestrian) {
        return currentPosition.distance(pedestrian.getCurrentPosition()) < (currentRadius + pedestrian.getCurrentRadius());
    }

    public double distance(Pedestrian pedestrian) {
        return distance(pedestrian.getCurrentPosition(), pedestrian.getCurrentRadius());
    }

    public double distance(Vector2D point, double pointRadius) {
        return currentPosition.distance(point) - (currentRadius + pointRadius);
    }

    public Vector2D getCurrentPosition() {
        return this.currentPosition;
    }

    public double getDesiredSpeed() {
        return this.desiredSpeed;
    }

    public double getCurrentRadius() {
        return this.currentRadius;
    }

    public double getMaxRadius() {
        return this.maxRadius;
    }

    public void setNextPosition(Vector2D nextPosition) {
        this.nextPosition = nextPosition;
    }

    public void setNextVelocity(Vector2D nextVelocity) {
        this.nextVelocity = nextVelocity;
    }
}
