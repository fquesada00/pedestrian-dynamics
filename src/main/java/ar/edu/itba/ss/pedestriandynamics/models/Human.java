package ar.edu.itba.ss.pedestriandynamics.models;

public class Human extends Pedestrian {
    private static double DESIRED_SPEED = 4;
    // TODO: Duda: qué radios tomamos?
    private static double MIN_RADIUS = 0.1;
    private static double MAX_RADIUS = 0.3;
    private static double BETA = 0.9;
    private static double TAU = 0.5;

    // obstacle coefficients
    private double zombieAp;
    private double zombieBp;
    private double humanAp;
    private double humanBp;
    private double wallAp;
    private double wallBp;

    public Human(double initialX, double initialY, double zombieAp, double zombieBp, double humanAp, double humanBp, double wallAp, double wallBp) {
        super(initialX, initialY, DESIRED_SPEED, MIN_RADIUS, MAX_RADIUS, BETA, TAU);
        this.zombieAp = zombieAp;
        this.zombieBp = zombieBp;
        this.humanAp = humanAp;
        this.humanBp = humanBp;
        this.wallAp = wallAp;
        this.wallBp = wallBp;
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

    public double getZombieAp() {
        return zombieAp;
    }

    public double getZombieBp() {
        return zombieBp;
    }

    public double getHumanAp() {
        return humanAp;
    }

    public double getHumanBp() {
        return humanBp;
    }

    public double getWallAp() {
        return wallAp;
    }

    public double getWallBp() {
        return wallBp;
    }
}
