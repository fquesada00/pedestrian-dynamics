package ar.edu.itba.ss.pedestriandynamics.models;

public enum ObstacleCoefficients {
    ZOMBIE(3000, 0.2), HUMAN(100, 0.2), WALL(500, 0.4);

    public double Ap;
    public double Bp;

    ObstacleCoefficients(double Ap, double Bp) {
        this.Ap = Ap;
        this.Bp = Bp;
    }
}
