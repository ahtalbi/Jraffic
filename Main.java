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
        
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}