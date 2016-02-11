package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	
	public static Scene scene;
	
	@Override
	public void start(Stage stage) {
		try {
			Image logo = new Image(getClass().getResourceAsStream("/resources/logo.png"));
			
			MainWindow root = new MainWindow(stage);
			scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			root.getStyleClass().add("group");

			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(scene);
			stage.getIcons().add(logo);
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
