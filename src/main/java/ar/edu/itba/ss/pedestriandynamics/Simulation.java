package ar.edu.itba.ss.pedestriandynamics;

import ar.edu.itba.ss.pedestriandynamics.models.Human;
import ar.edu.itba.ss.pedestriandynamics.models.Pedestrian;
import ar.edu.itba.ss.pedestriandynamics.models.Zombie;
import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Simulation {

    private final double ZOMBIE_SCAN_RADIUS = 4;
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
            Vector2D humanPos = Vector2D.randomFromPolar(2 * minPedestrianRadius + initialDistanceToZombie, roomRadius - minPedestrianRadius, 0, 2 * Math.PI, random)
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

            // calculate next state for every zombie
            for (Zombie zombie : zombies) {
                Human nextZombieTarget = getNextZombieTarget(zombie, ZOMBIE_SCAN_RADIUS);
                Vector2D nextTargetDirection;
                double nextZombieSpeed;

                // TODO: add case for collision
                if (nextZombieTarget == null) {
                    nextTargetDirection = Vector2D.randomFromPolar(0, roomRadius - zombie.getMaxRadius(),
                            0, 2 * Math.PI, random);
                    nextZombieSpeed = zombie.getInactiveSpeed();
                } else {
                    nextTargetDirection = nextZombieTarget.getCurrentPosition();
                    nextZombieSpeed = zombie.getDesiredSpeed();
                }

                Vector2D nextVelocity = nextTargetDirection.scale(1 / nextTargetDirection.length()).scale(nextZombieSpeed);
                zombie.setNextVelocity(nextVelocity);
            }

            // calculate next state for every human
            for (Human human : humans) {
                // heuristic like measure distances from the 5 nearest objects
            }

            // update pedestrians
            // move()
            // analizar infecciones

        }
    }

    private Human getNextZombieTarget(Zombie zombie, double scanRadius) {
        Human target = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Human human : humans) {
            double distance = zombie.distance(human);

            if (distance < scanRadius && distance < minDistance) {
                target = human;
                minDistance = distance;
            }
        }

        return target;
    }
}
