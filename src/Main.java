import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private final int size = 700;

    public void start(Stage stage) {
        Image image = new Image("file:assets/bg.jpg");
        ImageView background = new ImageView(image);

        Pane canvas = new Pane();
        canvas.getChildren().add(background);

        Scene scene = new Scene(canvas, size, size);
        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());
        CarsManager carsManager = new CarsManager(canvas, size);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> carsManager.addCar(Direction.UP);
                case DOWN -> carsManager.addCar(Direction.DOWN);
                case LEFT -> carsManager.addCar(Direction.LEFT);
                case RIGHT -> carsManager.addCar(Direction.RIGHT);
                case R -> carsManager.addRandomCar();
                case ESCAPE -> Platform.exit();
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lUpdate = 0;

            @Override
            public void handle(long now) {
                if (lUpdate == 0) {
                    lUpdate = now;
                    return;
                }

                carsManager.updateCars((now - lUpdate) / 1_000_000_000.0);
                lUpdate = now;
            }
        };

        timer.start();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}