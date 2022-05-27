package ar.edu.itba.ss.pedestriandynamics.models;

public class Human extends Pedestrian {

    public Human(double initialX, double initialY, double desiredSpeed, double minRadius, double maxRadius, double beta, double tau) {
        super(initialX, initialY, desiredSpeed, minRadius, maxRadius, beta, tau);
    }
}
