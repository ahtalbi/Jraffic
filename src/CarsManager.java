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

    private final Pane pane;
    private final List<Car> cars = new ArrayList<>();
    private final Random random = new Random();

    private record Route(Color color, Turn turn) {}

    private final Route[] routes = {
        new Route(Color.RED, Turn.LEFT),
        new Route(Color.YELLOW, Turn.LEFT),
        new Route(Color.GREEN, Turn.RIGHT),
        new Route(Color.BLUE, Turn.STRAIGHT)
    };

    public CarsManager(Pane pane, int screenSize) {
        this.pane = pane;
        this.screenSize = screenSize;
    }

    public void spawnCar(Direction direction) {
        Route route = routes[random.nextInt(routes.length)];
        double center = screenSize / 2.0;
        double x;
        double y;

        switch (direction) {
            case UP -> { x = center + laneOffset; y = screenSize; }
            case DOWN -> { x = center - laneOffset - carSize; y = -carSize; }
            case LEFT -> { x = screenSize; y = center - laneOffset - carSize; }
            case RIGHT -> { x = -carSize; y = center + laneOffset; }
            default -> throw new IllegalArgumentException("Unknown direction");
        }

        Car car = new Car(route.color(), direction, route.turn(), x, y, screenSize, laneOffset);
        cars.add(car);
        pane.getChildren().add(car.getRectangle());
    }

    public void spawnRandomCar() {
        Direction[] directions = Direction.values();
        Direction randomDirection = directions[random.nextInt(directions.length)];
        spawnCar(randomDirection);
    }

    public void updateCars(double elapsedSeconds) {
        Iterator<Car> iterator = cars.iterator();

        while (iterator.hasNext()) {
            Car car = iterator.next();
            car.move(elapsedSeconds);

            if (car.isOffScreen(screenSize)) {
                pane.getChildren().remove(car.getRectangle());
                iterator.remove();
            }
        }
    }
}