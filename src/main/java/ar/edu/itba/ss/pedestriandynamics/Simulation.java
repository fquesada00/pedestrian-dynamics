package ar.edu.itba.ss.pedestriandynamics;

import ar.edu.itba.ss.pedestriandynamics.models.Human;
import ar.edu.itba.ss.pedestriandynamics.models.ObstacleCoefficients;
import ar.edu.itba.ss.pedestriandynamics.models.Pedestrian;
import ar.edu.itba.ss.pedestriandynamics.models.Zombie;
import ar.edu.itba.ss.pedestriandynamics.utils.Constants;
import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

import java.util.*;

public class Simulation {

    private final static double ZOMBIE_SCAN_RADIUS = 4;
    private final double roomRadius;
    private final List<Human> humans;
    private final List<Zombie> zombies;

    public static void main(String[] args) {

    }

    public Simulation(double roomRadius, int totalHumans,
                      double minPedestrianRadius, double maxPedestrianRadius, double humanDesiredSpeed, double zombieDesiredSpeed,
                      double zombieInactiveSpeed, double beta, double tau, double initialDistanceToZombie) {

        this.roomRadius = roomRadius;

        this.zombies = new ArrayList<>();

        this.zombies.add(new Zombie(
                0, 0, zombieDesiredSpeed, minPedestrianRadius, maxPedestrianRadius, beta, tau, zombieInactiveSpeed));

        this.humans = generateInitialHumanPopulation(totalHumans, roomRadius, minPedestrianRadius,
                maxPedestrianRadius, beta, tau, humanDesiredSpeed, initialDistanceToZombie);

    }

    private List<Human> generateInitialHumanPopulation(int popSize, double roomRadius, double minPedestrianRadius, double maxPedestrianRadius,
                                                       double beta, double tau, double humanDesiredSpeed, double initialDistanceToZombie) {
        List<Human> humans = new ArrayList<>();

        Random random = new Random();

        while (humans.size() < popSize) {
            Vector2D humanPos = Vector2D.randomFromPolar(2 * minPedestrianRadius + initialDistanceToZombie, roomRadius - minPedestrianRadius, 0, 2 * Math.PI, random);
            Human newHuman = new Human(humanPos.x(), humanPos.y(), humanDesiredSpeed, minPedestrianRadius, maxPedestrianRadius, beta, tau);

            boolean overlaps = humans.stream().anyMatch(human -> human.overlaps(newHuman));

            if (!overlaps) {
                humans.add(newHuman);
            }
        }

        return humans;
    }

    public void simulate(double duration, double stepSize) {
        int steps = (int) Math.floor(duration / stepSize);

        Random random = new Random();

        for (int i = 0; i < steps; i++) {

            // analyze infections & collisions
            for (Human human : humans) {
                for (Human anotherHuman : humans) {
                    if (human.equals(anotherHuman)) continue;

                    if (human.overlaps(anotherHuman)) {
                        anotherHuman.setNextRadius(human.getMinRadius());
                    }
                }

                Vector2D nearestWall = computeNearestWallPosition(human.getCurrentPosition());

                if (human.distance(nearestWall, 0) < Constants.EPSILON) {
                    human.setNextRadius(human.getMinRadius());
                }

                for (Zombie zombie : zombies) {

                }
            }

            // calculate next state for every zombie
            for (Zombie zombie : zombies) {
                Human nextZombieTarget = getNextZombieTarget(zombie);
                Vector2D nextTargetDirection;
                double nextZombieSpeed;

                if (nextZombieTarget == null) {
                    nextZombieSpeed = zombie.getInactiveSpeed();

                    if (zombie.isWandering()) {
                        nextTargetDirection = zombie.getWanderTarget();
                    } else {
                        nextTargetDirection = Vector2D.randomFromPolar(0, roomRadius - zombie.getMaxRadius(),
                                0, 2 * Math.PI, random);
                        zombie.setWanderTarget(nextTargetDirection);
                    }

                } else {
                    nextTargetDirection = nextZombieTarget.getCurrentPosition();
                    nextZombieSpeed = zombie.getDesiredSpeed();
                    zombie.setWanderTarget(null);
                }

                Vector2D nextVelocity = nextTargetDirection.scale(1 / nextTargetDirection.length()).scale(nextZombieSpeed);
                zombie.setNextVelocity(nextVelocity);
            }

            // calculate next state for every human
            for (Human human : humans) {
                Vector2D eludeDirection = new Vector2D(0, 0);

                // add zombies
                zombies.forEach(zombie -> {
                    eludeDirection.add(computeEscapeDirectionTerm(
                            ObstacleCoefficients.ZOMBIE,
                            human.getCurrentPosition(),
                            zombie.getCurrentPosition()));
                });

                // add nearest wall
                eludeDirection.add(computeEscapeDirectionTerm(
                        ObstacleCoefficients.WALL,
                        human.getCurrentPosition(),
                        computeNearestWallPosition(human.getCurrentPosition()))
                );

                // add nearest 5 humans

                humans.forEach(h -> {
                    if (h == human) return;

                    eludeDirection.add(computeEscapeDirectionTerm(
                            ObstacleCoefficients.HUMAN,
                            human.getCurrentPosition(),
                            h.getCurrentPosition()));
                });

                human.setNextVelocity(eludeDirection.normalize().scale(human.getDesiredSpeed()));
            }

            // update pedestrians
            updatePedestrianPositions(stepSize);
        }
    }

    private Vector2D computeEscapeDirectionTerm(ObstacleCoefficients coefficients, Vector2D ownPosition, Vector2D obstaclePosition) {
        Vector2D direction = ownPosition.subtract(obstaclePosition);
        double dij = direction.length();
        Vector2D eij = direction.normalize();

        return eij.scale(coefficients.Ap * Math.exp(-dij / coefficients.Bp));
    }

    private Vector2D computeNearestWallPosition(Vector2D ownPosition) {
        double angle = ownPosition.xAxisAngle();

        return new Vector2D(roomRadius * Math.cos(angle), roomRadius * Math.sin(angle));
    }

    private Human getNextZombieTarget(Zombie zombie) {
        Human target = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Human human : humans) {
            double distance = zombie.distance(human);

            if (distance < ZOMBIE_SCAN_RADIUS && distance < minDistance) {
                target = human;
                minDistance = distance;
            }
        }

        return target;
    }

    private void updatePedestrianPositions(double stepSize) {
        humans.forEach(h -> h.move(stepSize));
        zombies.forEach(z -> z.move(stepSize));
    }
}
