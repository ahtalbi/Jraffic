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
                routes.get(Direction.LEFT).add(new Car(canvas, color, direction, screenSize, screenSize / 2 - carSize / 2 - carSize, screenSize));
                break;
            default:
                routes.get(Direction.RIGHT).add(new Car(canvas, color, direction, 0 - carSize, screenSize / 2 + carSize / 3, screenSize));
                break;
        }
    }

    private boolean canSpawn(Direction direction) {
        for (Car car : routes.get(direction)) {
            double distance = switch (direction) {
                case UP    -> screenSize - (car.getY() + carSize);
                case DOWN  -> car.getY() - (-carSize);
                case LEFT  -> screenSize - (car.getX() + carSize);
                case RIGHT -> car.getX() - (-carSize);
            };

            if (distance < carSize + safetyGap) {
                return false;
            }
        }
        return true;
    }

    public void setRandomCar() {
        Direction direction = Direction.random();
        CarColor color = CarColor.random();
        routes.get(direction).add(new Car(canvas, color, direction, screenSize / 2 + carSize / 3 , screenSize, screenSize));
    }

    // Methods

    private boolean hasCarAhead(Car current, List<Car> sameLaneCars) {
        for (Car other : sameLaneCars) {
            if (other == current) {
                continue;
            }

            double distance = switch (current.getDirection()) {
                case UP    -> current.getY() - (other.getY() + carSize);
                case DOWN  -> other.getY() - (current.getY() + carSize);
                case LEFT  -> current.getX() - (other.getX() + carSize);
                case RIGHT -> other.getX() - (current.getX() + carSize);
            };

            if (distance >= 0 && distance < safetyGap) {
                return true;
            }
        }
        return false;
    }

    public void updateCars(double timeBetweenFrames) {
        for (List<Car> cars : routes.values()) {
            Iterator<Car> iterator = cars.iterator();

            while (iterator.hasNext()) {
                Car car = iterator.next();
                car.move(timeBetweenFrames);

                if (car.hasExitedScreen()) {
                    car.removeFromCanvas();
                    iterator.remove();
                }
            }
        }
    }
}