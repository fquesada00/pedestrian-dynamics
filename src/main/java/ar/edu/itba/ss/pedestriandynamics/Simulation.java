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
    private static final String DEFAULT_DYNAMIC_OUTPUT_FILE_NAME = "dynamic.txt";
    private static final String DEFAULT_STATIC_OUTPUT_FILE_NAME = "static.txt";
    private final String dynamicOutputFileName;
    private final String staticOutputFileName;
    private final double roomRadius;
    private final List<Human> humans;
    private final List<Zombie> zombies;

    public Simulation(double roomRadius, int totalHumans,
            double minPedestrianRadius, double maxPedestrianRadius, double humanDesiredSpeed, double zombieDesiredSpeed,
            double zombieInactiveSpeed,
            double beta, double tau, double initialDistanceToZombie, String dynamicOutputFileName,
            String staticOutputFileName, Random random,
            boolean randomizeZombieObstacleCoefficients, boolean randomizeHumanObstacleCoefficients,
            boolean randomizeWallObstacleCoefficients) throws IOException {
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
        this.dynamicOutputFileName = dynamicOutputFileName;
        this.staticOutputFileName = staticOutputFileName;

        this.zombies = new ArrayList<>();

        this.zombies.add(new Zombie(
                0, 0));

        this.humans = generateInitialHumanPopulation(totalHumans, roomRadius, minPedestrianRadius,
                maxPedestrianRadius, beta, tau, humanDesiredSpeed, initialDistanceToZombie, random,
                randomizeZombieObstacleCoefficients, randomizeHumanObstacleCoefficients,
                randomizeWallObstacleCoefficients);

    }

    public static void main(String[] args) throws IOException {
        int defaultNumberOfHumans = 100;
        int numberOfHumans = Integer.parseInt(System.getProperty("nh", Integer.toString(defaultNumberOfHumans)));

        double defaultZombieDesiredSpeed = 3;
        double zombieDesiredSpeed = Double
                .parseDouble(System.getProperty("vdz", Double.toString(defaultZombieDesiredSpeed)));

        double defaultStepSize = 0.01;
        double stepSize = Double.parseDouble(System.getProperty("stepSize", Double.toString(defaultStepSize)));

        double defaultAnimationStep = 0.5;
        double animationStep = Double.parseDouble(System.getProperty("animationStep", Double.toString(defaultAnimationStep)));

        int defaultDuration = 200;
        int duration = Integer.parseInt(System.getProperty("duration", Integer.toString(defaultDuration)));

        long defaultSeed = -1;
        long seed = Long.parseLong(System.getProperty("seed", Long.toString(defaultSeed)));

        String dynamicOutputFileName = System.getProperty("dynamicOutputFileName", DEFAULT_DYNAMIC_OUTPUT_FILE_NAME);
        String staticOutputFileName = System.getProperty("staticOutputFileName", DEFAULT_STATIC_OUTPUT_FILE_NAME);

        // zombie obstacle coefficients
        double defaultZombieAp = ObstacleCoefficients.ZOMBIE.Ap;
        double zombieAp = Double.parseDouble(System.getProperty("zombieAp", Double.toString(defaultZombieAp)));

        double defaultZombieBp = ObstacleCoefficients.ZOMBIE.Bp;
        double zombieBp = Double.parseDouble(System.getProperty("zombieBp", Double.toString(defaultZombieBp)));

        // update zombie obstacle coefficients
        ObstacleCoefficients.ZOMBIE.Ap = zombieAp;
        ObstacleCoefficients.ZOMBIE.Bp = zombieBp;

        // human obstacle coefficients
        double defaultHumanAp = ObstacleCoefficients.HUMAN.Ap;
        double humanAp = Double.parseDouble(System.getProperty("humanAp", Double.toString(defaultHumanAp)));

        double defaultHumanBp = ObstacleCoefficients.HUMAN.Bp;
        double humanBp = Double.parseDouble(System.getProperty("humanBp", Double.toString(defaultHumanBp)));

        // update human obstacle coefficients
        ObstacleCoefficients.HUMAN.Ap = humanAp;
        ObstacleCoefficients.HUMAN.Bp = humanBp;

        // wall obstacle coefficients
        double defaultWallAp = ObstacleCoefficients.WALL.Ap;
        double wallAp = Double.parseDouble(System.getProperty("wallAp", Double.toString(defaultWallAp)));

        double defaultWallBp = ObstacleCoefficients.WALL.Bp;
        double wallBp = Double.parseDouble(System.getProperty("wallBp", Double.toString(defaultWallBp)));

        // update wall obstacle coefficients
        ObstacleCoefficients.WALL.Ap = wallAp;
        ObstacleCoefficients.WALL.Bp = wallBp;

        // use random obstacles coefficients per entity
        boolean randomizeZombieObstacleCoefficients = Boolean
                .parseBoolean(System.getProperty("randZ", "false"));
        boolean randomizeHumanObstacleCoefficients = Boolean
                .parseBoolean(System.getProperty("randH", "false"));
        boolean randomizeWallObstacleCoefficients = Boolean
                .parseBoolean(System.getProperty("randW", "false"));

        Random random = new Random();
        if (seed != -1) {
            random.setSeed(seed);
        }

        Simulation simulation = new Simulation(
                11, numberOfHumans, 0.1, 0.37, 4, zombieDesiredSpeed, 0.3, 0.9, 0.5, 1, dynamicOutputFileName,
                staticOutputFileName, random,
                randomizeZombieObstacleCoefficients, randomizeHumanObstacleCoefficients,
                randomizeWallObstacleCoefficients);

        // double stepSize = simulation.computeOptimalStepSize(minRadius,
        // humanDesiredSpeed, zombieDesiredSpeed);

        simulation.simulate(duration, stepSize, random, animationStep);
    }

    private List<Human> generateInitialHumanPopulation(int popSize, double roomRadius, double minPedestrianRadius,
            double maxPedestrianRadius,
            double beta, double tau, double humanDesiredSpeed, double initialDistanceToZombie, Random random,
            boolean randomizeZombieObstacleCoefficients, boolean randomizeHumanObstacleCoefficients,
            boolean randomizeWallObstacleCoefficients) throws IOException {
        List<Human> humans = new ArrayList<>();

        while (humans.size() < popSize) {
            Vector2D humanPos = Vector2D.randomFromPolar(2 * minPedestrianRadius + initialDistanceToZombie,
                    roomRadius - minPedestrianRadius, 0, 2 * Math.PI, random);

            // zombie obstacle coefficients
            double zombieAp = ObstacleCoefficients.ZOMBIE.Ap;
            double zombieBp = ObstacleCoefficients.ZOMBIE.Bp;

            if (randomizeZombieObstacleCoefficients) {
                zombieAp = random.nextInt(1000, 3000);
                zombieBp = random.nextDouble(0, 1);
            }


            // human obstacle coefficients
            double humanAp = ObstacleCoefficients.HUMAN.Ap;
            double humanBp = ObstacleCoefficients.HUMAN.Bp;

            if (randomizeHumanObstacleCoefficients) {
                humanAp = random.nextInt(500, 1000);
                humanBp = random.nextDouble(0, 1);
            }

            // wall obstacle coefficients
            double wallAp = ObstacleCoefficients.WALL.Ap;
            double wallBp = ObstacleCoefficients.WALL.Bp;

            if (randomizeWallObstacleCoefficients) {
                wallAp = random.nextInt(100, 1500);
                wallBp = random.nextDouble(0, 1);
            }

            Human newHuman = new Human(humanPos.x(), humanPos.y(), zombieAp, zombieBp, humanAp, humanBp, wallAp, wallBp);
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
        FileWriter fw = new FileWriter(DEFAULT_DYNAMIC_OUTPUT_FILE_NAME);
        fw.write("");
        fw.close();
    }

    public void simulate(double duration, double stepSize, Random random, double animationStep) throws IOException {
        int steps = (int) Math.floor(duration / stepSize);
        int skipSteps = (int) Math.floor(animationStep / stepSize);

        // clear file
        clearDynamicOutputFile();
        printStaticParameters();

        int i;
        for (i = 0; i < steps && humans.size() > 0; i++) {
            if (i % skipSteps == 0) {
                printSimulationStep(i);
            }

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
            updateZombiesNextTargets(random);
            //
            // TODO: Duda: es válido calcular esta velocidad de escape para zombies?
            // analyze collisions for zombies
            // updates zombies velocities and radii
            processZombiesCollisions(stepSize);

            // update pedestrians
            // updates humans positions and radius
            updatePedestrianPositions(stepSize);
        }

        if (i % skipSteps == 0) {
            printSimulationStep(i);
        }

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

            for (Human otherHuman : humans) {
                if (zombie.equals(otherHuman))
                    continue;
                if (zombie.overlaps(otherHuman)) {
                    collisionObstacles.add(otherHuman.getCurrentPosition());
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

    private void updateZombiesNextTargets(Random random) {
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

    private Vector2D computeEludeVelocity(Human human) {
        Vector2D eludeDirection = new Vector2D(0, 0);

        // add zombies
        List<Zombie> nearbyZombies = getNearbyZombies(zombies, human, HUMAN_SCAN_RADIUS);
        for (Zombie zombie : nearbyZombies) {

            ObstacleCoefficients.ZOMBIE.Ap = human.getZombieAp();
            ObstacleCoefficients.ZOMBIE.Bp = human.getZombieBp();

            eludeDirection = eludeDirection.add(computeEludeDirectionTerm(
                    ObstacleCoefficients.ZOMBIE,
                    human.getCurrentPosition(),
                    zombie.getCurrentPosition()));
        }

        // add nearest wall
        ObstacleCoefficients.WALL.Ap = human.getWallAp();
        ObstacleCoefficients.WALL.Bp = human.getWallBp();

        eludeDirection = eludeDirection.add(computeEludeDirectionTerm(
                ObstacleCoefficients.WALL,
                human.getCurrentPosition(),
                computeNearestWallPosition(human.getCurrentPosition())));

        // add nearest humans
        List<Human> nearbyHumans = getNearbyHumans(humans, human, HUMAN_SCAN_RADIUS);
        for (Human otherHuman : nearbyHumans) {
            if (otherHuman == human)
                continue;

            ObstacleCoefficients.HUMAN.Ap = human.getHumanAp();
            ObstacleCoefficients.HUMAN.Bp = human.getHumanBp();
            
            eludeDirection = eludeDirection.add(computeEludeDirectionTerm(
                    ObstacleCoefficients.HUMAN,
                    human.getCurrentPosition(),
                    otherHuman.getCurrentPosition()));
        }

        return eludeDirection.normalize().scale(human.computeNextSpeed());
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
        FileWriter fileWriter = new FileWriter(staticOutputFileName, false);

        fileWriter.write(String.format("%f\n%d\n%d\n", roomRadius, humans.size(), zombies.size()));

        fileWriter.close();
    }

    private void printSimulationStep(int stepNumber) throws IOException {
        FileWriter fileWriter = new FileWriter(dynamicOutputFileName, true);

        fileWriter.write(String.format("%d\n", stepNumber));

        for (Zombie zombie : zombies) {
            // fileWriter.write(String.format("%f\t%f\t%f\t255\t0\t0\n",
            // zombie.getCurrentPosition().x(), zombie.getCurrentPosition().y(),
            // ZOMBIE_SCAN_RADIUS));
            fileWriter.write(String.format("%f\t%f\t%f\t%s\n", zombie.getCurrentPosition().x(),
                    zombie.getCurrentPosition().y(), zombie.getCurrentRadius(), "z"));
        }
        for (Human human : humans) {
            fileWriter.write(String.format("%f\t%f\t%f\t%s\n", human.getCurrentPosition().x(),
                    human.getCurrentPosition().y(), human.getCurrentRadius(), human.isInfecting() ? "i" : "h"));
        }

        fileWriter.close();
    }
}
