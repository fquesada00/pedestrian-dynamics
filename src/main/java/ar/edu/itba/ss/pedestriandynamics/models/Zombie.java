package ar.edu.itba.ss.pedestriandynamics.models;

import ar.edu.itba.ss.pedestriandynamics.utils.Constants;
import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

public class Zombie extends Pedestrian {
    private static double INACTIVE_SPEED = 0.3;
    private static double DESIRED_SPEED = 3;
    private static double MIN_RADIUS = 0.1;
    private static double MAX_RADIUS = 0.37;
    private static double BETA = 0.9;
    private static double TAU = 0.5;


    private Vector2D wanderTarget;

    public Zombie(double initialX, double initialY) {
        super(initialX, initialY, DESIRED_SPEED, MIN_RADIUS, MAX_RADIUS, BETA, TAU);
    }

    public static Zombie fromHuman(Human human) {
        return new Zombie(
                human.currentPosition.x(),
                human.currentPosition.y());
    }

    public static double getInactiveSpeed() {
        return INACTIVE_SPEED;
    }

    public static void setParameters(double inactiveSpeed, double desiredSpeed, double minRadius, double maxRadius, double beta, double tau) {
        INACTIVE_SPEED = inactiveSpeed;
        DESIRED_SPEED = desiredSpeed;
        MIN_RADIUS = minRadius;
        MAX_RADIUS = maxRadius;
        BETA = beta;
        TAU = tau;
    }

    public boolean isDoneInfecting() {
        return isInfecting && remainingInfectionTime <= 0;
    }

    public void finishInfection() {
        this.isInfecting = false;
        this.remainingInfectionTime = 0;
    }

    public Vector2D getWanderTarget() {
        return wanderTarget;
    }

    public void setWanderTarget(Vector2D wanderTarget) {
        this.wanderTarget = wanderTarget;
    }

    public boolean isWandering() {
        return this.wanderTarget != null && !this.currentPosition.isEqualVector(this.wanderTarget, Constants.EPSILON);
    }
}
