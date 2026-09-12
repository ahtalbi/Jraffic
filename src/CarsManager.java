import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CarsManager {
    private final Pane canvas;
    private final int screenSize;
    private final int carSize = 40;
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
        CarColor carColor = CarColor.random();
        Color color = toColor(carColor);

        switch (direction) {
            case UP:
                routes.get(Direction.UP).add(new Car(canvas, color, direction, screenSize / 2 + carSize / 3 , screenSize));
                break;
            case DOWN:
                routes.get(Direction.DOWN).add(new Car(canvas, color, direction, screenSize / 2 - carSize / 3 - carSize, 0 - carSize));
                break;
            case LEFT:
                routes.get(Direction.LEFT).add(new Car(canvas, color, direction, screenSize + carSize , screenSize / 2 - carSize / 2 - carSize));
                break;
            default:
                routes.get(Direction.RIGHT).add(new Car(canvas, color, direction, 0 - carSize , screenSize / 2 + carSize / 3));
                break;
        }
    }

    public void setRandomCar() {

    }

    // Methods
    private Color toColor(CarColor carColor) {
        return switch (carColor) {
            case RED -> Color.RED;
            case GREEN -> Color.GREEN;
            case BLUE -> Color.BLUE;
        };
    }

    public void updateCars(double timeBetweenFrames) {
        for (List<Car> cars : routes.values()) {
            for (Car car : cars) {
                car.move(timeBetweenFrames);
            }
        }
    }
}