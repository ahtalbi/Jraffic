import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class CarsManager {
    private final Pane canvas;
    private final int screenSize;
    private final int carSize = Car.CAR_SIZE;
    private final int safetyGap = 35;
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
        if (countWaitingCars(direction) >= getLaneCapacity()) {
            return false;
        }

        for (Car car : routes.get(direction)) {
            if (car.hasTurned()) {
                continue;
            }

            double distance = switch (direction) {
                case UP    -> screenSize - (car.getY() + car.getCarSize());
                case DOWN  -> car.getY();
                case LEFT  -> screenSize - (car.getX() + car.getCarSize());
                case RIGHT -> car.getX();
            };

            if (distance < car.getCarSize() + safetyGap) {
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
        double currentCarSize = car.getCarSize();

        for (Car other : cars) {
            if (car == other || car.getDirection() != other.getDirection()) {
                continue;
            }

            boolean tooClose = switch (car.getDirection()) {
                case UP    -> other.getY() < car.getY() && car.getY() - distance < other.getY() + other.getCarSize() + safetyGap;
                case DOWN  -> other.getY() > car.getY() && car.getY() + distance + currentCarSize > other.getY() - safetyGap;
                case LEFT  -> other.getX() < car.getX() && car.getX() - distance < other.getX() + other.getCarSize() + safetyGap;
                case RIGHT -> other.getX() > car.getX() && car.getX() + distance + currentCarSize > other.getX() - safetyGap;
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

    public int getLaneCapacity() {
        return (int) Math.floor(295.0 / (carSize + safetyGap));
    }

    public boolean isApproaching(Car car, Direction direction) {
        if (car.hasTurned()) {
            return false;
        }

        return switch (direction) {
            case UP    -> car.getY() >= 405.0;
            case DOWN  -> car.getY() + car.getCarSize() <= 295.0;
            case LEFT  -> car.getX() >= 405.0;
            case RIGHT -> car.getX() + car.getCarSize() <= 295.0;
        };
    }

    public int countWaitingCars(Direction direction) {
        int count = 0;
        for (Car car : routes.get(direction)) {
            if (isApproaching(car, direction)) {
                count++;
            }
        }
        return count;
    }

    public boolean isIntersectionClear() {
        for (List<Car> list : routes.values()) {
            for (Car car : list) {
                if (car.getX() < 405.0 && car.getX() + car.getCarSize() > 295.0
                        && car.getY() < 405.0 && car.getY() + car.getCarSize() > 295.0) {
                    return false;
                }
            }
        }
        return true;
    }
}
