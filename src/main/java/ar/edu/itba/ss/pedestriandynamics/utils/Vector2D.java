package ar.edu.itba.ss.pedestriandynamics.utils;

import java.util.Random;

public record Vector2D(double x, double y) {
    public static Vector2D randomFromPolar(double lowerRadius, double upperRadius, double lowerAngle, double upperAngle, Random random) {
        double angle = random.nextDouble(lowerAngle, upperAngle);
        double radius = random.nextDouble(lowerRadius, upperRadius);
        
        double x = radius * Math.cos(angle);
        double y = radius * Math.sin(angle);

        return new Vector2D(x, y);
    }

    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vector2D scale(double factor) {
        return new Vector2D(this.x * factor, this.y * factor);
    }

    public double length() {
        return Math.sqrt(this.dot(this));
    }

    public double distance(Vector2D other) {
        return this.subtract(other).length();
    }

    public Vector2D normalize() {
        if (this.length() == 0) {
            return this;
        }

        return this.scale(1.0 / this.length());
    }

    public double angle(Vector2D other) {
        double angle = Math.acos(this.dot(other) / (this.length() * other.length()));
        if (this.x * other.y - this.y * other.x < 0) {
            angle = -angle;
        }
        return angle;
    }

    public double xAxisAngle() {
        return Math.atan2(this.y, this.x);
    }

    public boolean isEqualVector(Vector2D other, double epsilon) {

        return other != null && Math.abs(this.x - other.x) < epsilon && Math.abs(this.y - other.y) < epsilon;
    }
}