import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Car {
    private final int carSize = 40;
    private final double speed = 100;

    private final Rectangle rectangle;
    private final Turn turn;
    private final Direction exitDirection;
    private final double pivot;

    private Direction direction;
    private boolean hasTurned = false;

    public Car(Color color, Direction direction, Turn turn, double x, double y, int screenSize, int laneOffset) {
        this.direction = direction;
        this.turn = turn;

        rectangle = new Rectangle(carSize, carSize);
        rectangle.setFill(color);
        rectangle.setX(x);
        rectangle.setY(y);

        this.exitDirection = rotate(direction, turn);
        this.pivot = laneCoordinate(exitDirection, screenSize, laneOffset);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void move(double elapsedSeconds) {
        if (turn != Turn.STRAIGHT && !hasTurned && reachedPivot()) {
            applyTurn();
        }

        double distance = speed * elapsedSeconds;
        switch (direction) {
            case UP -> rectangle.setY(rectangle.getY() - distance);
            case DOWN -> rectangle.setY(rectangle.getY() + distance);
            case LEFT -> rectangle.setX(rectangle.getX() - distance);
            case RIGHT -> rectangle.setX(rectangle.getX() + distance);
        }
    }

    private boolean reachedPivot() {
        return switch (direction) {
            case UP -> rectangle.getY() <= pivot;
            case DOWN -> rectangle.getY() >= pivot;
            case LEFT -> rectangle.getX() <= pivot;
            case RIGHT -> rectangle.getX() >= pivot;
        };
    }

    private void applyTurn() {
        hasTurned = true;

        if (direction == Direction.UP || direction == Direction.DOWN) {
            rectangle.setY(pivot);
        } else {
            rectangle.setX(pivot);
        }

        direction = exitDirection;
    }

    private double laneCoordinate(Direction d, int screenSize, int laneOffset) {
        double center = screenSize / 2.0;
        return switch (d) {
            case UP, RIGHT -> center + laneOffset;
            case DOWN, LEFT -> center - laneOffset - carSize;
        };
    }

    private Direction rotate(Direction current, Turn turn) {
        return switch (turn) {
            case STRAIGHT -> current;
            case LEFT -> switch (current) {
                case UP -> Direction.LEFT;
                case LEFT -> Direction.DOWN;
                case DOWN -> Direction.RIGHT;
                case RIGHT -> Direction.UP;
            };
            case RIGHT -> switch (current) {
                case UP -> Direction.RIGHT;
                case RIGHT -> Direction.DOWN;
                case DOWN -> Direction.LEFT;
                case LEFT -> Direction.UP;
            };
        };
    }

    public boolean isOffScreen(int screenSize) {
        return rectangle.getX() > screenSize || rectangle.getX() + carSize < 0 || rectangle.getY() > screenSize || rectangle.getY() + carSize < 0;
    }
}