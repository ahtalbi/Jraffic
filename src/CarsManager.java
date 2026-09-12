import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CarsManager {

    private final int screenSize;

    private final int carSize = 40;
    private final int laneOffset = 20;
    private final int safetyGap = 15;

    private final int laneLength;
    private final int laneCapacity;

    private final Pane pane;

    private final List<Car> cars = new ArrayList<>();

    private final Random random = new Random();

    private record Route(Color color, Turn turn) {}

    private final Route[] routes = {
        new Route(Color.RED, Turn.LEFT),
        new Route(Color.GREEN, Turn.RIGHT),
        new Route(Color.BLUE, Turn.STRAIGHT)
    };

    // Intersection zone
    private final double intersectionMin;
    private final double intersectionMax;

    // Traffic lights
    private final TrafficLight[] lights = new TrafficLight[4];

    // Normal rotation:
    // UP -> RIGHT -> DOWN -> LEFT
    private final Direction[] rotation = {
        Direction.UP,
        Direction.RIGHT,
        Direction.DOWN,
        Direction.LEFT
    };

    private int currentPhase = 0;

    private boolean clearing = false;

    private double phaseTimer = 0;

    private final double baseGreenTime = 5.0;
    private final double clearanceTime = 2.0;

    public CarsManager(Pane pane, int screenSize) {

        this.pane = pane;
        this.screenSize = screenSize;

        this.laneLength = screenSize / 2;
        this.laneCapacity =
                laneLength / (carSize + safetyGap);

        this.intersectionMin =
                screenSize / 2.0 - laneOffset - carSize;

        this.intersectionMax =
                screenSize / 2.0 + laneOffset + carSize;

        initTrafficLights();
    }

    private void initTrafficLights() {

        int gap = 5;
        int lightSize = 12;

        // UP - from south
        lights[Direction.UP.ordinal()] =
                new TrafficLight(
                        intersectionMax + gap,
                        intersectionMax + gap,
                        true
                );

        // DOWN - from north
        lights[Direction.DOWN.ordinal()] =
                new TrafficLight(
                        intersectionMin - lightSize - gap,
                        intersectionMin - lightSize - gap,
                        false
                );

        // LEFT - from east
        lights[Direction.LEFT.ordinal()] =
                new TrafficLight(
                        intersectionMax + gap,
                        intersectionMin - lightSize - gap,
                        false
                );

        // RIGHT - from west
        lights[Direction.RIGHT.ordinal()] =
                new TrafficLight(
                        intersectionMin - lightSize - gap,
                        intersectionMax + gap,
                        false
                );

        for (TrafficLight light : lights) {
            pane.getChildren().add(light.getRect());
        }
    }

    // ─────────────────────────────────────────────
    // TRAFFIC LIGHT LOGIC
    // ─────────────────────────────────────────────

    private void updateTrafficLights(double elapsed) {

        phaseTimer += elapsed;

        if (clearing) {

            /*
             * All lights are red.
             * Wait until the intersection is clear.
             */
            if (phaseTimer >= clearanceTime
                    && isIntersectionClear()) {

                clearing = false;
                phaseTimer = 0;

                /*
                 * Find the next direction that
                 * actually has cars.
                 */
                Direction nextDirection =
                        findNextDirectionWithCars();

                /*
                 * If no direction has cars,
                 * continue normal rotation.
                 */
                if (nextDirection == null) {

                    currentPhase =
                            (currentPhase + 1)
                                    % rotation.length;

                    nextDirection =
                            rotation[currentPhase];
                }

                currentPhase =
                        getPhaseIndex(nextDirection);

                setSingleGreen(nextDirection);
            }

        } else {

            /*
             * Current green phase finished.
             */
            if (phaseTimer >= getGreenDuration()) {

                clearing = true;
                phaseTimer = 0;

                // All lights red during clearance.
                setAllRed();
            }
        }
    }

    /*
     * Find the next direction with cars.
     *
     * We keep the normal rotation:
     *
     * UP -> RIGHT -> DOWN -> LEFT
     *
     * Starting after the current direction,
     * check each direction.
     *
     * Empty lane = skip it.
     * Lane with at least one car = choose it.
     */
    private Direction findNextDirectionWithCars() {

        for (int i = 1; i <= rotation.length; i++) {

            int index =
                    (currentPhase + i)
                            % rotation.length;

            Direction direction =
                    rotation[index];

            int carsInLane =
                    countCarsInDirection(direction);

            if (carsInLane > 0) {
                return direction;
            }
        }

        /*
         * No cars anywhere.
         */
        return null;
    }

    private int getPhaseIndex(Direction direction) {

        for (int i = 0; i < rotation.length; i++) {

            if (rotation[i] == direction) {
                return i;
            }
        }

        return currentPhase;
    }

    private void setAllRed() {

        for (TrafficLight light : lights) {
            light.setGreen(false);
        }
    }

    private void setSingleGreen(Direction direction) {

        setAllRed();

        lights[direction.ordinal()]
                .setGreen(true);
    }

    private boolean isIntersectionClear() {

        for (Car car : cars) {

            if (car.getX() + carSize > intersectionMin
                    && car.getX() < intersectionMax
                    && car.getY() + carSize > intersectionMin
                    && car.getY() < intersectionMax) {

                return false;
            }
        }

        return true;
    }

    /*
     * Green time.
     *
     * More than 3 cars = extra green time.
     */
    private double getGreenDuration() {

        Direction active =
                rotation[currentPhase];

        int activeCount =
                countCarsInDirection(active);

        if (activeCount > 3) {
            return baseGreenTime + 3.0;
        }

        return baseGreenTime;
    }

    private int countCarsInDirection(Direction direction) {

        int count = 0;

        for (Car car : cars) {

            if (car.getDirection() == direction
                    && !car.hasTurned()) {

                count++;
            }
        }

        return count;
    }

    // ─────────────────────────────────────────────
    // STOP AT TRAFFIC LIGHT
    // ─────────────────────────────────────────────

    private boolean shouldStopAtLight(Car car) {

        if (car.hasTurned()) {
            return false;
        }

        /*
         * Green = go.
         */
        if (lights[car.getDirection().ordinal()]
                .isGreen()) {

            return false;
        }

        /*
         * Stop only near the stop line.
         */
        return switch (car.getDirection()) {

            case UP ->
                    car.getY() >= intersectionMax
                    && car.getY()
                    < intersectionMax + safetyGap;

            case DOWN ->
                    car.getY() + carSize
                    <= intersectionMin
                    && car.getY() + carSize
                    > intersectionMin - safetyGap;

            case LEFT ->
                    car.getX() >= intersectionMax
                    && car.getX()
                    < intersectionMax + safetyGap;

            case RIGHT ->
                    car.getX() + carSize
                    <= intersectionMin
                    && car.getX() + carSize
                    > intersectionMin - safetyGap;
        };
    }

    // ─────────────────────────────────────────────
    // SPAWN
    // ─────────────────────────────────────────────

    public void spawnCar(Direction direction) {

        if (!canSpawn(direction)) {
            return;
        }

        Route route =
                routes[random.nextInt(routes.length)];

        double center =
                screenSize / 2.0;

        double x;
        double y;

        switch (direction) {

            case UP -> {
                x = center + laneOffset;
                y = screenSize;
            }

            case DOWN -> {
                x = center
                        - laneOffset
                        - carSize;
                y = -carSize;
            }

            case LEFT -> {
                x = screenSize;
                y = center
                        - laneOffset
                        - carSize;
            }

            case RIGHT -> {
                x = -carSize;
                y = center + laneOffset;
            }

            default ->
                    throw new IllegalArgumentException(
                            "Unknown direction"
                    );
        }

        Car car = new Car(
                route.color(),
                direction,
                route.turn(),
                x,
                y,
                screenSize,
                laneOffset
        );

        cars.add(car);

        pane.getChildren().add(
                car.getRectangle()
        );
    }

    private boolean canSpawn(Direction direction) {

        int count = 0;

        for (Car car : cars) {

            if (car.getDirection() != direction
                    || car.hasTurned()) {
                continue;
            }

            count++;

            double distance =
                    distanceToSpawn(
                            car,
                            direction
                    );

            if (distance < carSize + safetyGap) {
                return false;
            }
        }

        return count < laneCapacity;
    }

    private double distanceToSpawn(
            Car car,
            Direction direction) {

        return switch (direction) {

            case UP ->
                    screenSize
                    - car.getY()
                    - carSize;

            case DOWN ->
                    car.getY() + carSize;

            case LEFT ->
                    screenSize
                    - car.getX()
                    - carSize;

            case RIGHT ->
                    car.getX() + carSize;
        };
    }

    public void spawnRandomCar() {

        Direction[] directions =
                Direction.values();

        Direction randomDirection =
                directions[
                        random.nextInt(
                                directions.length
                        )
                ];

        spawnCar(randomDirection);
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────

    public void updateCars(
            double elapsedSeconds) {

        updateTrafficLights(
                elapsedSeconds
        );

        for (Car car : cars) {

            /*
             * Stop at red light.
             */
            if (shouldStopAtLight(car)) {
                continue;
            }

            /*
             * Keep safe distance.
             */
            if (hasCarAhead(car)) {
                continue;
            }

            /*
             * Move.
             */
            car.move(elapsedSeconds);
        }

        Iterator<Car> iterator =
                cars.iterator();

        while (iterator.hasNext()) {

            Car car = iterator.next();

            if (car.isOffScreen(screenSize)) {

                pane.getChildren().remove(
                        car.getRectangle()
                );

                iterator.remove();
            }
        }
    }

    // ─────────────────────────────────────────────
    // SAFETY DISTANCE
    // ─────────────────────────────────────────────

    private boolean hasCarAhead(Car current) {

        for (Car other : cars) {

            if (other == current) {
                continue;
            }

            if (isInSweptArea(current, other)) {
                return true;
            }
        }

        return false;
    }

    private boolean isInSweptArea(
            Car current,
            Car other) {

        double lookAhead =
                carSize + safetyGap;

        double sMinX =
                current.getX();

        double sMaxX =
                current.getX()
                + carSize;

        double sMinY =
                current.getY();

        double sMaxY =
                current.getY()
                + carSize;

        switch (current.getDirection()) {

            case UP ->
                    sMinY -= lookAhead;

            case DOWN ->
                    sMaxY += lookAhead;

            case LEFT ->
                    sMinX -= lookAhead;

            case RIGHT ->
                    sMaxX += lookAhead;
        }

        return sMinX
                < other.getX() + carSize
                && sMaxX
                > other.getX()
                && sMinY
                < other.getY() + carSize
                && sMaxY
                > other.getY();
    }
}