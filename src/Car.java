import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Car {
    private final int screenSize;
    private final int carSize = 40;
    private final double speed = 100;
    private final Rectangle rectangle;
    private final CarColor carColor;
    private final Pane canvas;
    private Direction direction;
    private boolean hasTurned = false;

    // Constructors
    public Car(Pane canvas, CarColor carColor, Direction direction, int x, int y, int screenSize) {
        this.canvas = canvas;
        this.screenSize = screenSize;
        this.carColor = carColor;
        this.direction = direction;

        rectangle = new Rectangle(carSize, carSize);
        rectangle.setFill(toColor(carColor));
        rectangle.setX(x);
        rectangle.setY(y);

        canvas.getChildren().add(rectangle);
    }

    // Getters

    // Setters
    public void move(double timeBetweenFrames) {
        checkTurn();

        double distance = speed * timeBetweenFrames;
        switch (direction) {
            case UP    -> rectangle.setY(rectangle.getY() - distance);
            case DOWN  -> rectangle.setY(rectangle.getY() + distance);
            case LEFT  -> rectangle.setX(rectangle.getX() - distance);
            case RIGHT -> rectangle.setX(rectangle.getX() + distance);
        }
    }

    // Methods
    private void checkTurn() {
        if (hasTurned || carColor == CarColor.BLUE) {
            return;
        }

        boolean atTurnPoint = switch (direction) {
            case UP -> (carColor == CarColor.RED) ? rectangle.getY() <= screenSize / 2.0 - carSize - carSize / 2.0 : rectangle.getY() <= screenSize / 2.0 + carSize / 3.0;
            case DOWN -> (carColor == CarColor.RED) ? rectangle.getY() >= screenSize / 2.0 + carSize / 3.0 : rectangle.getY() >= screenSize / 2.0 - carSize - carSize / 3.0;
            case LEFT -> (carColor == CarColor.RED) ? rectangle.getX() <= screenSize / 2.0 - carSize / 3.0 - carSize : rectangle.getX() <= screenSize / 2.0 + carSize + carSize / 3.0 - carSize;
            case RIGHT -> (carColor == CarColor.RED) ? rectangle.getX() >= screenSize / 2.0 + carSize / 3.0 : rectangle.getX() >= screenSize / 2.0 - carSize - carSize / 3.0;
        };

        if (atTurnPoint) {
            direction = (carColor == CarColor.RED) ? turnLeft(direction) : turnRight(direction);
            hasTurned = true;
        }
    }

    private Direction turnLeft(Direction current) {
        return switch (current) {
            case UP    -> Direction.LEFT;
            case LEFT  -> Direction.DOWN;
            case DOWN  -> Direction.RIGHT;
            case RIGHT -> Direction.UP;
        };
    }

    private Direction turnRight(Direction current) {
        return switch (current) {
            case UP    -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN  -> Direction.LEFT;
            case LEFT  -> Direction.UP;
        };
    }

    private Color toColor(CarColor carColor) {
        return switch (carColor) {
            case RED -> Color.RED;
            case GREEN -> Color.GREEN;
            case BLUE -> Color.BLUE;
        };
    }
}