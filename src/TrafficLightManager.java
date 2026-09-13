import javafx.scene.layout.Pane;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TrafficLightManager {
    private static final double timer = 6.0;
    private static final double IDLE_TIMER = 3.0;
    private static final double MIN_PASS_TIME = 2.0;
    private static final double STARVATION_TIME = 20.0;
    private static final double ALL_RED_DURATION = 1.5;    
    private static final double MAX_ALL_RED_DURATION = 2.5;

    private static final double STOP_UP = 405.0;
    private static final double STOP_LEFT = 405.0;

    private final Map<Direction, TrafficLight> lights;
    private final Direction[] LIGHTS = { Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT };
    private final double[] lastTimeLightChanged = { 0.0, 0.0, 0.0, 0.0 };
    private int activeLight = 0;
    private double e = 0.0;

    private boolean allRed = false;
    private double allRedTimer = 0.0;
    private int pendingLight = 0;

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

    public void update(double dt, CarsManager carsManager) {
        if (allRed) {
            allRedTimer += dt;
            for (int i = 0; i < lastTimeLightChanged.length; i++) {
                lastTimeLightChanged[i] += dt;
            }

            boolean clear = (carsManager == null) || carsManager.isIntersectionClear();
            if (allRedTimer >= ALL_RED_DURATION && (clear || allRedTimer >= MAX_ALL_RED_DURATION)) {
                allRed = false;
                allRedTimer = 0.0;
                activeLight = pendingLight;
                e = 0.0;
                lastTimeLightChanged[activeLight] = 0.0;
                setSingleGreen(LIGHTS[activeLight]);
            }
            return;
        }

        Map<String, Integer> carsByDirection = getCarsByDirection(carsManager);

        e += dt;

        for (int i = 0; i < lastTimeLightChanged.length; i++) {
            if (i != activeLight) {
                lastTimeLightChanged[i] += dt;
            }
        }

        boolean empty = true;
        for (Direction direction : LIGHTS) {
            if (carsByDirection.getOrDefault(direction.toString(), 0) > 0) {
                empty = false;
                break;
            }
        }

        if (empty && e >= IDLE_TIMER) {
            triggerSwitch((activeLight + 1) % LIGHTS.length);
            return;
        }

        if (e < timer - 2) {
            return;
        }

        int d = -1;
        double worstWait = -1;

        for (int i = 0; i < LIGHTS.length; i++) {
            if (i == activeLight) continue;

            int waitingCars = carsByDirection.getOrDefault(LIGHTS[i].toString(), 0);
            if (lastTimeLightChanged[i] >= STARVATION_TIME && waitingCars > 0) {
                if (lastTimeLightChanged[i] > worstWait) {
                    worstWait = lastTimeLightChanged[i];
                    d = i;
                }
            }
        }

        if (d == -1 && carsManager != null) {
            int capacity = carsManager.getLaneCapacity();
            for (int i = 0; i < LIGHTS.length; i++) {
                if (i == activeLight) continue;
                if (carsByDirection.getOrDefault(LIGHTS[i].toString(), 0) >= capacity) {
                    d = i;
                    break;
                }
            }
        }

        if (d != -1) {
            triggerSwitch(d);
            return;
        }

        if (!empty && carsByDirection.getOrDefault(LIGHTS[activeLight].toString(), 0) == 0) {
            int b = activeLight;
            int maxCars = -1;
            for (int i = 0; i < LIGHTS.length; i++) {
                int count = carsByDirection.getOrDefault(LIGHTS[i].toString(), 0);
                if (count > maxCars) {
                    maxCars = count;
                    b = i;
                }
            }

            triggerSwitch(b);
            return;
        }

        if (e >= timer) {
            int b = activeLight;
            int maxCars = -1;
            for (int i = 0; i < LIGHTS.length; i++) {
                int count = carsByDirection.getOrDefault(LIGHTS[i].toString(), 0);
                if (count > maxCars) {
                    maxCars = count;
                    b = i;
                }
            }

            triggerSwitch(b);
        }
    }

    private void triggerSwitch(int nextLight) {
        if (nextLight == activeLight) {
            e = 0.0;
            return;
        }
        allRed = true;
        allRedTimer = 0.0;
        pendingLight = nextLight;
        setAllRed();
    }

    public boolean isGreen(Direction direction) {
        if (allRed) {
            return false;
        }
        return direction == LIGHTS[activeLight];
    }

    public boolean isAllRed() {
        return allRed;
    }

    public Map<String, Integer> getCarsByDirection(CarsManager carsManager) {
        Map<String, Integer> mp = new HashMap<>();
        for (Direction direction : Direction.values()) {
            mp.put(direction.toString(), 0);
        }

        if (carsManager != null) {
            for (Direction direction : Direction.values()) {
                mp.put(direction.toString(), carsManager.countWaitingCars(direction));
            }
        }

        return mp;
    }

    public boolean shouldStop(Car car, Direction spawnDirection, double timeBetweenFrames) {
        if (car.hasTurned()) {
            return false;
        }

        if (isGreen(spawnDirection)) {
            return false;
        }

        double distance = 100 * timeBetweenFrames;
        double carSize = car.getCarSize();

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
                double stopDown = 295.0 - carSize;
                if (car.getY() <= stopDown) {
                    if (car.getY() + distance >= stopDown) {
                        car.setY(stopDown);
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
                double stopRight = 295.0 - carSize;
                if (car.getX() <= stopRight) {
                    if (car.getX() + distance >= stopRight) {
                        car.setX(stopRight);
                        return true;
                    }
                }
                break;
        }

        return false;
    }

    public TrafficLight getTrafficLight(Direction direction) {
        return lights.get(direction);
    }

    public Direction getActiveDirection() {
        return LIGHTS[activeLight];
    }

    public double getGreenDuration(CarsManager carsManager) {
        return timer;
    }

    private void setAllRed() {
        for (TrafficLight light : lights.values()) {
            light.setGreen(false);
        }
    }

    private void setSingleGreen(Direction dir) {
        for (Map.Entry<Direction, TrafficLight> entry : lights.entrySet()) {
            entry.getValue().setGreen(entry.getKey() == dir);
        }
    }
}
