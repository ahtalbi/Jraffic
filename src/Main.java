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

        Pane pane = new Pane();
        pane.getChildren().add(background);

        Scene scene = new Scene(pane, size, size);

        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        CarsManager carsManager = new CarsManager(pane, size);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> carsManager.spawnCar(Direction.UP);
                case DOWN -> carsManager.spawnCar(Direction.DOWN);
                case LEFT -> carsManager.spawnCar(Direction.LEFT);
                case RIGHT -> carsManager.spawnCar(Direction.RIGHT);
                case R -> carsManager.spawnRandomCar();
                case ESCAPE -> Platform.exit();
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double elapsed = (now - lastUpdate) / 1_000_000_000.0;
                carsManager.updateCars(elapsed);
                lastUpdate = now;
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