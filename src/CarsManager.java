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
    public void addCar(Direction direction) {
        Color 

        switch (direction) {
            case UP:
                routes.get(Direction.UP).add(new Car(Color.RED, direction, 100, 100, screenSize));
                break;
            case DOWN:
                routes.get(Direction.DOWN).add(new Car(Color.BLUE, direction, 200, 100, screenSize));
                break;
            case LEFT:
                routes.get(Direction.LEFT).add(new Car(Color.GREEN, direction, 300, 100, screenSize));
                break;
            default:
                routes.get(Direction.RIGHT).add(new Car(Color.YELLOW, direction, 400, 100, screenSize));
                break;
        }
    }

    public void addRandomCar() {

    }
}