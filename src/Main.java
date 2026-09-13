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

    @Override
    public void start(Stage stage) {
        Image image = new Image("file:assets/bg.jpg");
        ImageView background = new ImageView(image);

        Pane canvas = new Pane();
        canvas.getChildren().add(background);

        Scene scene = new Scene(canvas, size, size);
        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        CarsManager carsManager = new CarsManager(canvas, size);
        TrafficLightManager trafficLightManager = new TrafficLightManager(canvas);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> carsManager.setCar(Direction.UP);
                case DOWN -> carsManager.setCar(Direction.DOWN);
                case LEFT -> carsManager.setCar(Direction.LEFT);
                case RIGHT -> carsManager.setCar(Direction.RIGHT);
                case R -> carsManager.setRandomCar();
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

                double elapsedSeconds = (now - lUpdate) / 1_000_000_000.0;
                trafficLightManager.update(elapsedSeconds, carsManager);
                carsManager.updateCars(elapsedSeconds, trafficLightManager);
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