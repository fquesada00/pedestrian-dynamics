package ar.edu.itba.ss.pedestriandynamics.models;

public enum ObstacleCoefficients {
    ZOMBIE(3000, 0.2), HUMAN(2000, 0.2), WALL(500, 0.4);

    public final double Ap;
    public final double Bp;

    ObstacleCoefficients(double Ap, double Bp) {
        this.Ap = Ap;
        this.Bp = Bp;
    }
}
