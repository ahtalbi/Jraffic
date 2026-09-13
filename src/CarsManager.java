import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class CarsManager {
    private final Pane canvas;
    private final int screenSize;
    private final int carSize = 40;
    private final int safetyGap = 20;
    private final Map<Direction, List<Car>> routes;

    // Constructor
    public CarsManager(Pane canvas, int screenSize) {
        this.canvas = canvas;
        this.screenSize = screenSize;

        routes = new EnumMap<>(Direction.class);

        for (Direction direction : Direction.values()) {
            routes.put(direction, new ArrayList<>());
        }
    }

    // Getters

    // Setters
    public void setCar(Direction direction) {
        if (!canSpawn(direction)) {
            return;
        }

        CarColor color = CarColor.random();

        switch (direction) {
            case UP:
                routes.get(Direction.UP).add(new Car(canvas, color, direction, screenSize / 2 + carSize / 3, screenSize, screenSize));
                break;
            case DOWN:
                routes.get(Direction.DOWN).add(new Car(canvas, color, direction, screenSize / 2 - carSize / 3 - carSize, 0 - carSize, screenSize));
                break;
            case LEFT:
                routes.get(Direction.LEFT).add(new Car(canvas, color, direction, screenSize, screenSize / 2 - carSize / 3 - carSize, screenSize));
                break;
            default:
                routes.get(Direction.RIGHT).add(new Car(canvas, color, direction, 0 - carSize, screenSize / 2 + carSize / 3, screenSize));
                break;
        }
    }

    public void setRandomCar() {
        setCar(Direction.random());
    }

    // Methods
    private boolean canSpawn(Direction direction) {
        for (Car car : routes.get(direction)) {
            if (car.hasTurned()) {
                continue;
            }

            double distance = switch (direction) {
                case UP    -> screenSize - (car.getY() + carSize);
                case DOWN  -> car.getY();
                case LEFT  -> screenSize - (car.getX() + carSize);
                case RIGHT -> car.getX();
            };

            if (distance < carSize + safetyGap) {
                return false;
            }
        }
        return true;
    }

    public void updateCars(double timeBetweenFrames, TrafficLightManager trafficLightManager) {
        for (Map.Entry<Direction, List<Car>> entry : routes.entrySet()) {
            Direction direction = entry.getKey();
            List<Car> cars = entry.getValue();
            Iterator<Car> iterator = cars.iterator();

            while (iterator.hasNext()) {
                Car car = iterator.next();
                if (trafficLightManager != null && trafficLightManager.shouldStop(car, direction, timeBetweenFrames)) {
                    continue;
                }

                if (hasSafeGap(car, cars, timeBetweenFrames)) {
                    car.move(timeBetweenFrames);
                }

                if (car.hasExitedScreen()) {
                    car.removeFromCanvas();
                    iterator.remove();
                }
            }
        }
    }

    public void updateCars(double timeBetweenFrames) {
        updateCars(timeBetweenFrames, null);
    }

    private boolean hasSafeGap(Car car, List<Car> cars, double timeBetweenFrames) {
        double distance = 100 * timeBetweenFrames;

        for (Car other : cars) {
            if (car == other || car.getDirection() != other.getDirection()) {
                continue;
            }

            boolean tooClose = switch (car.getDirection()) {
                case UP    -> other.getY() < car.getY() && car.getY() - distance < other.getY() + carSize + safetyGap;
                case DOWN  -> other.getY() > car.getY() && car.getY() + distance + carSize > other.getY() - safetyGap;
                case LEFT  -> other.getX() < car.getX() && car.getX() - distance < other.getX() + carSize + safetyGap;
                case RIGHT -> other.getX() > car.getX() && car.getX() + distance + carSize > other.getX() - safetyGap;
            };

            if (tooClose) {
                return false;
            }
        }

        return true;
    }

    // =========================================================================
    // Traffic Light Helpers
    // =========================================================================

    public Map<Direction, List<Car>> getRoutes() {
        return routes;
    }

    public int countWaitingCars(Direction direction) {
        int count = 0;
        for (Car car : routes.get(direction)) {
            if (car.hasTurned()) {
                continue;
            }

            boolean isApproaching = switch (direction) {
                case UP    -> car.getY() >= 405.0;
                case DOWN  -> car.getY() <= 255.0;
                case LEFT  -> car.getX() >= 405.0;
                case RIGHT -> car.getX() <= 255.0;
            };

            if (isApproaching) {
                count++;
            }
        }
        return count;
    }

    public boolean isIntersectionClear() {
        for (List<Car> list : routes.values()) {
            for (Car car : list) {
                if (car.getX() < 405.0 && car.getX() + carSize > 295.0
                        && car.getY() < 405.0 && car.getY() + carSize > 295.0) {
                    return false;
                }
            }
        }
        return true;
    }
}
