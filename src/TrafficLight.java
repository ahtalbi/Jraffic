import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TrafficLight {
    private static final int SIZE = 16;
    private final Rectangle rect;
    private boolean green;

    public TrafficLight(double x, double y, boolean initialGreen) {
        this.green = initialGreen;
        rect = new Rectangle(x, y, SIZE, SIZE);
        rect.setArcWidth(4);
        rect.setArcHeight(4);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        update();
    }

    public Rectangle getRect() { return rect; }
    public boolean isGreen() { return green; }

    public void setGreen(boolean green) {
        this.green = green;
        update();
    }

    private void update() {
        rect.setFill(green ? Color.LIME : Color.RED);
    }
}
