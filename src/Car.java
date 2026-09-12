import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Car {
    private final int carSize = 40;
    private final double speed = 100; // pixels per second
    private final int screenSize;
    private final int laneOffset;

    private final Rectangle rectangle;
    private final Turn turn;
    private Direction direction;
    private boolean hasTurned = false;

    public Car(Color color, Direction direction, Turn turn, double x, double y, int screenSize, int laneOffset) {
        this.direction = direction;
        this.turn = turn;
        this.screenSize = screenSize;
        this.laneOffset = laneOffset;

        rectangle = new Rectangle(carSize, carSize);
        rectangle.setFill(color);
        rectangle.setX(x);
        rectangle.setY(y);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void move(double elapsedSeconds) {
        if (!hasTurned && reachedPivot()) {
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

    private double pivot() {
        return screenSize / 2.0 - carSize / 2.0;
    }

    private boolean reachedPivot() {
        double p = pivot();
        return switch (direction) {
            case UP -> rectangle.getY() <= p;
            case DOWN -> rectangle.getY() >= p;
            case LEFT -> rectangle.getX() <= p;
            case RIGHT -> rectangle.getX() >= p;
        };
    }

    private void applyTurn() {
        hasTurned = true;
        if (turn == Turn.STRAIGHT) return; // blue: no heading change

        Direction newDirection = rotate(direction, turn);
        double p = pivot();

        // Whichever axis was fixed before becomes the travel axis now (and vice versa).
        if (direction == Direction.UP || direction == Direction.DOWN) {
            rectangle.setX(p);
            rectangle.setY(laneCoordinate(newDirection));
        } else {
            rectangle.setY(p);
            rectangle.setX(laneCoordinate(newDirection));
        }

        direction = newDirection;
    }

    private double laneCoordinate(Direction d) {
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
        return rectangle.getX() > screenSize
                || rectangle.getX() + carSize < 0
                || rectangle.getY() > screenSize
                || rectangle.getY() + carSize < 0;
    }
}