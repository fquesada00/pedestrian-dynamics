package ar.edu.itba.ss.pedestriandynamics.models;

public enum ObstacleCoefficients {
    ZOMBIE(1.0, 1.0), HUMAN(1.0, 1.0), WALL(1.0, 1.0);

    public final double Ap;
    public final double Bp;

    ObstacleCoefficients(double Ap, double Bp) {
        this.Ap = Ap;
        this.Bp = Bp;
    }
}
