package ar.edu.itba.ss.pedestriandynamics.models;

public class Human extends Pedestrian {
    private static double DESIRED_SPEED = 4;
    // TODO: Duda: qué radios tomamos?
    private static double MIN_RADIUS = 0.1;
    private static double MAX_RADIUS = 0.37;
    private static double BETA = 0.9;
    private static double TAU = 0.5;

    public Human(double initialX, double initialY) {
        super(initialX, initialY, DESIRED_SPEED, MIN_RADIUS, MAX_RADIUS, BETA, TAU);
    }

    public boolean transitionToZombie() {
        return isInfecting && remainingInfectionTime <= 0;
    }

    public static void setParameters(double desiredSpeed, double minRadius, double maxRadius, double beta, double tau) {
        DESIRED_SPEED = desiredSpeed;
        MIN_RADIUS = minRadius;
        MAX_RADIUS = maxRadius;
        BETA = beta;
        TAU = tau;
    }
}
