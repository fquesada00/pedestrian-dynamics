package ar.edu.itba.ss.pedestriandynamics.models;

public class Zombie extends Pedestrian {
    private final double inactiveSpeed;

    public Zombie(double initialX, double initialY, double desiredSpeed, double minRadius, double maxRadius, double beta, double tau, double inactiveSpeed) {
        super(initialX, initialY, desiredSpeed, minRadius, maxRadius, beta, tau);
        this.inactiveSpeed = inactiveSpeed;
    }

    public double getInactiveSpeed() {
        return inactiveSpeed;
    }
}
