import javafx.scene.layout.Pane;
import java.util.EnumMap;
import java.util.Map;

public class TrafficLightManager {
    private static final double IDLE_GREEN_TIME = 3.0;
    private static final double BASE_GREEN_TIME = 5.0;
    private static final double CONGESTED_GREEN_TIME = 8.0;
    private static final double CLEARANCE_TIMEOUT = 1.5;

    private static final double STOP_UP = 405.0;
    private static final double STOP_DOWN = 255.0;
    private static final double STOP_LEFT = 405.0;
    private static final double STOP_RIGHT = 255.0;

    private final Map<Direction, TrafficLight> lights;
    private final Direction[] rotation = { Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT };
    private int currentPhase = 0;
    private double phaseTimer = 0.0;
    private boolean clearing = false;

    public TrafficLightManager(Pane canvas) {
        lights = new EnumMap<>(Direction.class);
        initLights(canvas);
    }

    private void initLights(Pane canvas) {
        lights.put(Direction.DOWN, new TrafficLight(275, 275, false));
        lights.put(Direction.LEFT, new TrafficLight(409, 275, false));
        lights.put(Direction.UP, new TrafficLight(409, 409, true));
        lights.put(Direction.RIGHT, new TrafficLight(275, 409, false));

        for (TrafficLight light : lights.values()) {
            canvas.getChildren().add(light.getRect());
        }
    }

    public void update(double elapsedSeconds, CarsManager carsManager) {
        phaseTimer += elapsedSeconds;

        if (clearing) {
            boolean clear = (carsManager == null) || carsManager.isIntersectionClear();
            if (clear || phaseTimer >= CLEARANCE_TIMEOUT) {
                clearing = false;
                phaseTimer = 0.0;
                advancePhase();
            }
        } else {
            double duration = getGreenDuration(carsManager);
            if (phaseTimer >= duration) {
                phaseTimer = 0.0;
                boolean clear = (carsManager == null) || carsManager.isIntersectionClear();
                if (!clear) {
                    clearing = true;
                    setAllRed();
                } else {
                    advancePhase();
                }
            }
        }
    }

    public boolean shouldStop(Car car, Direction spawnDirection, double timeBetweenFrames) {
        if (car.hasTurned()) {
            return false;
        }

        if (isGreen(spawnDirection)) {
            return false;
        }

        double distance = 100 * timeBetweenFrames;

        switch (spawnDirection) {
            case UP:
                if (car.getY() >= STOP_UP) {
                    if (car.getY() - distance <= STOP_UP) {
                        car.setY(STOP_UP);
                        return true;
                    }
                }
                break;
            case DOWN:
                if (car.getY() <= STOP_DOWN) {
                    if (car.getY() + distance >= STOP_DOWN) {
                        car.setY(STOP_DOWN);
                        return true;
                    }
                }
                break;
            case LEFT:
                if (car.getX() >= STOP_LEFT) {
                    if (car.getX() - distance <= STOP_LEFT) {
                        car.setX(STOP_LEFT);
                        return true;
                    }
                }
                break;
            case RIGHT:
                if (car.getX() <= STOP_RIGHT) {
                    if (car.getX() + distance >= STOP_RIGHT) {
                        car.setX(STOP_RIGHT);
                        return true;
                    }
                }
                break;
        }

        return false;
    }

    public boolean isGreen(Direction direction) {
        TrafficLight light = lights.get(direction);
        return light != null && light.isGreen();
    }

    public TrafficLight getTrafficLight(Direction direction) {
        return lights.get(direction);
    }

    public Direction getActiveDirection() {
        return rotation[currentPhase];
    }

    private double getGreenDuration(CarsManager carsManager) {
        if (carsManager == null) {
            return IDLE_GREEN_TIME;
        }

        Direction active = rotation[currentPhase];
        int waiting = carsManager.countWaitingCars(active);

        // When no one exists, switch every 3 seconds to the next light
        if (waiting == 0) {
            return IDLE_GREEN_TIME;
        } else if (waiting > 3) {
            return CONGESTED_GREEN_TIME;
        } else {
            return BASE_GREEN_TIME;
        }
    }

    private void advancePhase() {
        currentPhase = (currentPhase + 1) % rotation.length;
        setSingleGreen(rotation[currentPhase]);
    }

    private void setAllRed() {
        for (TrafficLight light : lights.values()) {
            light.setGreen(false);
        }
    }

    private void setSingleGreen(Direction dir) {
        setAllRed();
        TrafficLight light = lights.get(dir);
        if (light != null) {
            light.setGreen(true);
        }
    }
}
