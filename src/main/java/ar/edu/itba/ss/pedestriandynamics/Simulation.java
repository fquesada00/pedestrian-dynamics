package ar.edu.itba.ss.pedestriandynamics;

import ar.edu.itba.ss.pedestriandynamics.models.Human;
import ar.edu.itba.ss.pedestriandynamics.models.ObstacleCoefficients;
import ar.edu.itba.ss.pedestriandynamics.models.Pedestrian;
import ar.edu.itba.ss.pedestriandynamics.models.Zombie;
import ar.edu.itba.ss.pedestriandynamics.utils.Constants;
import ar.edu.itba.ss.pedestriandynamics.utils.Vector2D;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {

    private final static double ZOMBIE_SCAN_RADIUS = 4;
    private final static double HUMAN_SCAN_RADIUS = 4;
    private static final String DYNAMIC_OUTPUT_FILE_NAME = "dynamic.txt";
    private static final String STATIC_OUTPUT_FILE_NAME = "static.txt";
    private final double roomRadius;
    private final List<Human> humans;
    private final List<Zombie> zombies;

    public Simulation(double roomRadius, int totalHumans,
            double minPedestrianRadius, double maxPedestrianRadius, double humanDesiredSpeed, double zombieDesiredSpeed,
            double zombieInactiveSpeed,
            double beta, double tau, double initialDistanceToZombie) {
        Zombie.setParameters(
                zombieInactiveSpeed,
                zombieDesiredSpeed,
                minPedestrianRadius,
                maxPedestrianRadius,
                beta,
                tau);

        Human.setParameters(
                humanDesiredSpeed,
                minPedestrianRadius,
                maxPedestrianRadius,
                beta,
                tau);

        this.roomRadius = roomRadius;

        this.zombies = new ArrayList<>();

        this.zombies.add(new Zombie(
                0, 0));

        this.humans = generateInitialHumanPopulation(totalHumans, roomRadius, minPedestrianRadius,
                maxPedestrianRadius, beta, tau, humanDesiredSpeed, initialDistanceToZombie);

    }

    public static void main(String[] args) throws IOException {
        int defaultNumberOfHumans = 100;
        int numberOfHumans = Integer.parseInt(System.getProperty("nh", Integer.toString(defaultNumberOfHumans)));

        double defaultZombieDesiredSpeed = 3;
        double zombieDesiredSpeed = Double.parseDouble(System.getProperty("vdz", Double.toString(defaultZombieDesiredSpeed)));

        double defaultStepSize = 0.01;
        double stepSize = Double.parseDouble(System.getProperty("stepSize", Double.toString(defaultStepSize)));

        int defaultDuration = 200;
        int duration = Integer.parseInt(System.getProperty("duration", Integer.toString(defaultDuration)));

        Simulation simulation = new Simulation(
                11, numberOfHumans, 0.1, 0.37, 4, zombieDesiredSpeed, 0.3, 0.9, 0.5, 1);

        // double stepSize = simulation.computeOptimalStepSize(minRadius,
        // humanDesiredSpeed, zombieDesiredSpeed);

        simulation.simulate(duration, stepSize);
    }

    private List<Human> generateInitialHumanPopulation(int popSize, double roomRadius, double minPedestrianRadius,
            double maxPedestrianRadius,
            double beta, double tau, double humanDesiredSpeed, double initialDistanceToZombie) {
        List<Human> humans = new ArrayList<>();

        Random random = new Random();

        while (humans.size() < popSize) {
            Vector2D humanPos = Vector2D.randomFromPolar(2 * minPedestrianRadius + initialDistanceToZombie,
                    roomRadius - minPedestrianRadius, 0, 2 * Math.PI, random);
            Human newHuman = new Human(humanPos.x(), humanPos.y());

            boolean overlaps = humans.stream().anyMatch(human -> human.overlaps(newHuman));

            if (!overlaps) {
                humans.add(newHuman);
            }
        }

        return humans;
    }

    private double computeOptimalStepSize(double minRadius, double humanDesiredSpeed, double zombieDesiredSpeed) {
        return 0.5 * minRadius / Math.max(humanDesiredSpeed, zombieDesiredSpeed);
    }

    private void clearDynamicOutputFile() throws IOException {
        FileWriter fw = new FileWriter(DYNAMIC_OUTPUT_FILE_NAME);
        fw.write("");
        fw.close();
    }

    public void simulate(double duration, double stepSize) throws IOException {
        int steps = (int) Math.floor(duration / stepSize);
        // clear file
        clearDynamicOutputFile();
        printStaticParameters();

        for (int i = 0; i < steps && humans.size() > 0; i++) {
            printSimulationStep(i);
            // convert humans to zombies
            List<Zombie> newZombies = humans.stream().filter(Human::transitionToZombie).map(Zombie::fromHuman)
                    .collect(Collectors.toList());
            zombies.addAll(newZombies);
            zombies.forEach(zombie -> {
                if (zombie.isDoneInfecting()) {
                    zombie.finishInfection();
                }
            });
            humans.removeIf(Human::transitionToZombie);

            // analyze infections & collisions for humans & elude
            // set infection variables
            // set next velocities and radii for humans
            processHumansCollisions(stepSize);

            // calculate next target for every zombie
            updateZombiesNextTargets();
            //
            // TODO: Duda: es válido calcular esta velocidad de escape para zombies?
            // analyze collisions for zombies
            // updates zombies velocities and radii
            processZombiesCollisions(stepSize);

            // update pedestrians
            // updates humans positions and radius
            updatePedestrianPositions(stepSize);
        }

        printSimulationStep(steps);

        System.out.println("Humans: " + humans.size());
    }

    private void processHumansCollisions(double stepSize) {
        for (Human human : humans) {
            if (human.isInfecting()) {
                human.decreaseRemainingInfectionTime(stepSize);
                continue;
            }

            List<Vector2D> collisionObstacles = new ArrayList<>();

            for (Human otherHuman : humans) {
                if (human.equals(otherHuman))
                    continue;

                if (human.overlaps(otherHuman)) {
                    collisionObstacles.add(otherHuman.getCurrentPosition());
                }
            }

            Vector2D nearestWall = computeNearestWallPosition(human.getCurrentPosition());

            if (human.distance(nearestWall, 0) < Constants.EPSILON) {
                collisionObstacles.add(nearestWall);
            }

            for (Zombie zombie : zombies) {
                // TODO: Duda: Qué pasa si más de un zombie está en contacto con un humano?
                if (human.overlaps(zombie)) {
                    collisionObstacles.add(zombie.getCurrentPosition());
                    zombie.setNextRadius(zombie.getMinRadius());

                    // sets next velocity to 0
                    human.startInfection();
                    zombie.startInfection();
                }
            }

            // for collisions
            if (collisionObstacles.size() > 0) {
                human.setNextRadius(human.getMinRadius());

                if (!human.isInfecting()) {
                    human.setNextVelocity(computeEscapeVelocity(human, collisionObstacles));
                }
            } else {
                // for collision avoidance
                human.setNextRadius(human.computeNextRadius(stepSize));
                human.setNextVelocity(computeEludeVelocity(human));
            }
        }
    }

    private void processZombiesCollisions(double stepSize) {
        for (Zombie zombie : zombies) {
            if (zombie.isInfecting()) {
                zombie.decreaseRemainingInfectionTime(stepSize);
                continue;
            }

            List<Vector2D> collisionObstacles = new ArrayList<>();

            Vector2D nearestWall = computeNearestWallPosition(zombie.getCurrentPosition());

            if (zombie.distance(nearestWall, 0) < Constants.EPSILON) {
                collisionObstacles.add(nearestWall);
            }

            for (Zombie otherZombie : zombies) {
                if (zombie.equals(otherZombie))
                    continue;
                // TODO: Duda: Qué pasa si más de un zombie está en contacto con un humano?
                if (zombie.overlaps(otherZombie)) {
                    collisionObstacles.add(otherZombie.getCurrentPosition());
                }
            }

            // for collisions
            if (collisionObstacles.size() > 0) {
                zombie.setNextRadius(zombie.getMinRadius());
                // zombie.setWanderTarget(null);
                zombie.setNextVelocity(computeEscapeVelocity(zombie, collisionObstacles));
            } else {
                // TODO: Duda: el radio incrementa con el tiempo como el humano o es
                // instantaneo?
                zombie.setNextRadius(zombie.computeNextRadius(stepSize));
            }

        }
    }

    private void updateZombiesNextTargets() {
        Random random = new Random();

        for (Zombie zombie : zombies) {
            if (zombie.isInfecting()) {
                continue;
            }

            Human nextZombieTarget = getNextZombieTarget(zombie);
            Vector2D nextTargetDirection;
            double nextZombieSpeed;

            if (nextZombieTarget == null) {
                nextZombieSpeed = Zombie.getInactiveSpeed();

                if (zombie.isWandering()) {
                    nextTargetDirection = zombie.getWanderTarget();
                } else {
                    nextTargetDirection = Vector2D.randomFromPolar(0, roomRadius - zombie.getMaxRadius(),
                            0, 2 * Math.PI, random);
                    zombie.setWanderTarget(nextTargetDirection);
                }

            } else {
                nextTargetDirection = nextZombieTarget.getCurrentPosition();
                // TODO: Duda: La velocidad cambia instantaneamente o depende del radio?
                nextZombieSpeed = zombie.getDesiredSpeed();
                zombie.setWanderTarget(null);
            }

            //
            Vector2D nextVelocity = nextTargetDirection.subtract(zombie.getCurrentPosition()).normalize()
                    .scale(nextZombieSpeed);
            zombie.setNextVelocity(nextVelocity);
        }
    }

    private List<Zombie> getNearbyZombies(List<Zombie> zombies, Pedestrian pedestrian, double scanRadius) {
        List<Zombie> nearbyZombies = new ArrayList<>();

        for (Zombie zombie : zombies) {
            if (pedestrian.equals(zombie))
                continue;

            if (pedestrian.distance(zombie.getCurrentPosition(), zombie.getCurrentRadius()) < scanRadius) {
                nearbyZombies.add(zombie);
            }
        }
        return nearbyZombies;
    }

    private List<Human> getNearbyHumans(List<Human> humans, Pedestrian pedestrian, double scanRadius) {
        List<Human> nearbyHumans = new ArrayList<>();

        for (Human human : humans) {
            if (pedestrian.equals(human))
                continue;

            if (pedestrian.distance(human.getCurrentPosition(), human.getCurrentRadius()) < scanRadius) {
                nearbyHumans.add(human);
            }
        }
        return nearbyHumans;
    }

    private Vector2D computeEludeVelocity(Pedestrian pedestrian) {
        Vector2D eludeDirection = new Vector2D(0, 0);

        // add zombies
        List<Zombie> nearbyZombies = getNearbyZombies(zombies, pedestrian, HUMAN_SCAN_RADIUS);
        for (Zombie zombie : nearbyZombies) {
            eludeDirection = eludeDirection.add(computeEludeDirectionTerm(
                    ObstacleCoefficients.ZOMBIE,
                    pedestrian.getCurrentPosition(),
                    zombie.getCurrentPosition()));
        }

        // add nearest wall
        eludeDirection = eludeDirection.add(computeEludeDirectionTerm(
                ObstacleCoefficients.WALL,
                pedestrian.getCurrentPosition(),
                computeNearestWallPosition(pedestrian.getCurrentPosition())));

        // add nearest humans
        List<Human> nearbyHumans = getNearbyHumans(humans, pedestrian, HUMAN_SCAN_RADIUS);
        for (Human human : nearbyHumans) {
            if (human == pedestrian)
                continue;

            eludeDirection = eludeDirection.add(computeEludeDirectionTerm(
                    ObstacleCoefficients.HUMAN,
                    pedestrian.getCurrentPosition(),
                    human.getCurrentPosition()));
        }

        return eludeDirection.normalize().scale(pedestrian.computeNextSpeed());
    }

    private Vector2D computeEludeDirectionTerm(ObstacleCoefficients coefficients, Vector2D ownPosition,
            Vector2D obstaclePosition) {
        Vector2D direction = ownPosition.subtract(obstaclePosition);
        double dij = direction.length();
        Vector2D eij = direction.normalize();

        return eij.scale(coefficients.Ap * Math.exp(-dij / coefficients.Bp));
    }

    private Vector2D computeEscapeVelocity(Pedestrian pedestrian, List<Vector2D> obstacles) {
        Vector2D escapeDirection = new Vector2D(0, 0);

        for (Vector2D obstacle : obstacles) {
            escapeDirection = escapeDirection.add(pedestrian.getCurrentPosition().subtract(obstacle).normalize());
        }

        return escapeDirection.normalize().scale(pedestrian.getDesiredSpeed());
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
        humans.forEach(h -> {
            h.updateRadius();
            h.move(stepSize);
        });
        zombies.forEach(z -> {
            z.updateRadius();
            z.move(stepSize);
        });
    }

    private void printStaticParameters() throws IOException {
        FileWriter fileWriter = new FileWriter(Simulation.STATIC_OUTPUT_FILE_NAME, false);

        fileWriter.write(String.format("%f\n%d\n%d\n", roomRadius, humans.size(), zombies.size()));

        fileWriter.close();
    }

    private void printSimulationStep(int stepNumber) throws IOException {
        FileWriter fileWriter = new FileWriter(Simulation.DYNAMIC_OUTPUT_FILE_NAME, true);

        fileWriter.write(String.format("%d\n", stepNumber));

        for (Zombie zombie : zombies) {
            // fileWriter.write(String.format("%f\t%f\t%f\t255\t0\t0\n",
            // zombie.getCurrentPosition().x(), zombie.getCurrentPosition().y(),
            // ZOMBIE_SCAN_RADIUS));
            fileWriter.write(String.format("%f\t%f\t%f\t%s\n", zombie.getCurrentPosition().x(),
                    zombie.getCurrentPosition().y(), zombie.getCurrentRadius(), zombie.isInfecting() ? "i" : "z"));
        }
        for (Human human : humans) {
            fileWriter.write(String.format("%f\t%f\t%f\t%s\n", human.getCurrentPosition().x(),
                    human.getCurrentPosition().y(), human.getCurrentRadius(), human.isInfecting() ? "i" : "h"));
        }

        fileWriter.close();
    }
}
