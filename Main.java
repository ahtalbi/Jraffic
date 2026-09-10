import javafx.application.Application;
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

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    System.out.println("UP pressed");
                    break;
                case DOWN:
                    System.out.println("DOWN pressed");
                    break;
                case LEFT:
                    System.out.println("LEFT pressed");
                    break;
                case RIGHT:
                    System.out.println("RIGHT pressed");
                    break;
                case R:
                    System.out.println("R pressed");
                    break;
            }
        });

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}