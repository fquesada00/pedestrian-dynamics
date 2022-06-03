package ar.edu.itba.ss.pedestriandynamics.models;

public enum ObstacleCoefficients {
    ZOMBIE(2000, 0.5), HUMAN(1, 0.5), WALL(500, 0.5);

    public final double Ap;
    public final double Bp;

    ObstacleCoefficients(double Ap, double Bp) {
        this.Ap = Ap;
        this.Bp = Bp;
    }
}
