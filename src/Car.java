import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Car {
    private final int screenSize;
    private final int carSize = 40;
    private final double speed = 100;
    private final Rectangle rectangle;
    private final Direction direction;
    private final Pane canvas;
    
    // Constructors
    public Car(Pane canvas, Color color, Direction direction, int x, int y, int screenSize) {
        this.canvas = canvas;
        this.screenSize = screenSize;
        this.direction = direction;
        
        rectangle = new Rectangle(carSize, carSize);
        rectangle.setFill(color);
        rectangle.setX(x);
        rectangle.setY(y);

        canvas.getChildren().add(rectangle);
    }

    // Getters

    // Setters
    public void move(double timeBetweenFrames) {
        double distance = speed * timeBetweenFrames;
        switch (direction) {
            case UP    -> rectangle.setY(rectangle.getY() - distance);
            case DOWN  -> rectangle.setY(rectangle.getY() + distance);
            case LEFT  -> rectangle.setX(rectangle.getX() - distance);
            case RIGHT -> rectangle.setX(rectangle.getX() + distance);
        }
    }
}