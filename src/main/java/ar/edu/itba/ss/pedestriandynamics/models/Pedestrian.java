package ar.edu.itba.ss.pedestriandynamics.models;

import ar.edu.itba.ss.pedestriandynamics.utils.Constants;
import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

public abstract class Pedestrian {

    protected final double desiredSpeed;
    protected final double maxRadius;
    protected final double minRadius;
    protected final double beta;
    protected final double tau;
    protected Vector2D currentPosition;
    protected Vector2D nextVelocity;
    protected double nextRadius;
    protected double currentRadius;
    protected boolean isInfecting;
    protected double remainingInfectionTime;

    public Pedestrian(double initialX, double initialY, double desiredSpeed, double minRadius, double maxRadius, double beta, double tau) {
        this.desiredSpeed = desiredSpeed;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.beta = beta;
        this.tau = tau;
        this.currentPosition = new Vector2D(initialX, initialY);
        this.isInfecting = false;
    }

    public void move(double stepSize) {
        this.currentPosition = this.currentPosition.add(this.nextVelocity.scale(stepSize));
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

    public double getMinRadius() {
        return this.minRadius;
    }

    public void setNextRadius(double radius) {
        this.nextRadius = radius;
    }

    public void setNextVelocity(Vector2D nextVelocity) {
        this.nextVelocity = nextVelocity;
    }

    public double computeNextRadius(double stepSize) {
        if (currentRadius < maxRadius)
            return currentRadius + maxRadius * stepSize / tau;

        return maxRadius;
    }

    public double computeNextSpeed() {
        return desiredSpeed * (Math.pow((nextRadius - minRadius) / (maxRadius - minRadius), beta));
    }

    public boolean isInfecting() {
        return this.isInfecting;
    }

    public void setInfecting(boolean isInfecting) {
        this.isInfecting = true;
    }

    public void startInfection() {
        this.isInfecting = true;
        this.remainingInfectionTime = Constants.INFECTION_TIME;
        this.nextVelocity = new Vector2D(0, 0);
    }

    public double getRemainingInfectionTime() {
        return this.remainingInfectionTime;
    }

    public void decreaseRemainingInfectionTime(double time) {
        this.remainingInfectionTime -= time;
    }

    public double getBeta() {
        return beta;
    }

    public double getTau() {
        return tau;
    }

    public void updateRadius() {
        this.currentRadius = this.nextRadius;
    }
}
