import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Car {
    private final int carSize = 40;
    private final double speed = 100;
    private final Rectangle rectangle;

    public Car(Color color, Direction direction, int x, int y, int screenSize) {
        this.direction = direction;

        rectangle = new Rectangle(carSize, carSize);
        rectangle.setFill(color);
        rectangle.setX(x);
        rectangle.setY(y);
    }

}