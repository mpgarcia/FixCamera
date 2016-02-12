package application;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainWindow extends Pane {
	public MainWindow(Stage stage) {
		
		EventHandler<MouseEvent> mouseEntered = new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	application.Main.scene.setCursor(Cursor.HAND);
		    }
		};
		EventHandler<MouseEvent> mouseExited = new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	application.Main.scene.setCursor(Cursor.DEFAULT);
		    }
		};
		
		Text camaraText = new Text("UNIDAD DE LA CAMARA");
        camaraText.setFont(Font.font("Helvetica", 20));
        camaraText.setWrappingWidth(300);
        camaraText.setFill(Color.GRAY);
        camaraText.setTextAlignment(TextAlignment.CENTER);
        camaraText.setTranslateX(50);
        camaraText.setTranslateY(130);
		
		Text destinoText = new Text("DESTINO DEL VIDEO");
		destinoText.setFont(Font.font("Helvetica", 20));
		destinoText.setWrappingWidth(300);
		destinoText.setFill(Color.GRAY);
		destinoText.setTextAlignment(TextAlignment.CENTER);
		destinoText.setTranslateX(50);
		destinoText.setTranslateY(230);

		ObservableList<String> options = FXCollections.observableArrayList();
		ComboBox<String> unidades = new ComboBox<String>(options);
		unidades.getStyleClass().add("ndtcq");
		unidades.setPrefWidth(100);
		unidades.setTranslateX(145);
		unidades.setTranslateY(150);
		unidades.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEntered);
		unidades.addEventFilter(MouseEvent.MOUSE_EXITED, mouseExited);
		
		Label destino = new Label("SELECCIONE");
		destino.setText("C:/SISTEMA/CAMARAS");
		destino.getStyleClass().add("seleccion");
		destino.setPrefWidth(260);
		destino.setTranslateX(65);
		destino.setTranslateY(250);
		destino.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEntered);
		destino.addEventFilter(MouseEvent.MOUSE_EXITED, mouseExited);
		destino.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			File directory = new File("C:/SISTEMA/CAMARAS");
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	DirectoryChooser fchoose = new DirectoryChooser();
		    	if (directory.exists()) { fchoose.setInitialDirectory(directory); }
		    	File selectedDirectory = fchoose.showDialog(stage);
		    	if (selectedDirectory != null) {
		    		destino.setText(selectedDirectory.getAbsolutePath());
		    		directory = selectedDirectory;
		    	}
		    }
		});
		
		Label aceptar = new Label("ACEPTAR");
		aceptar.getStyleClass().add("seleccion");
		aceptar.setStyle("-fx-background-color: #009ad8; -fx-text-fill: #ffffff");
		aceptar.setPrefWidth(100);
		aceptar.setTranslateX(145);
		aceptar.setTranslateY(330);
		aceptar.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEntered);
		aceptar.addEventFilter(MouseEvent.MOUSE_EXITED, mouseExited);
		aceptar.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	if (unidades.getSelectionModel().getSelectedItem() != null && !destino.getText().equals("SELECCIONE")) {
			    	CopyWindow copy = new CopyWindow(stage, unidades.getSelectionModel().getSelectedItem(), destino.getText());
			    	copy.getStyleClass().add("group");
			    	application.Main.scene.setRoot(copy);
		    	}
		    }
		});
		
		Image imageSVLogo = new Image(getClass().getResourceAsStream("/resources/svlogo.png"));
        Label SVLogo = new Label("", new ImageView(imageSVLogo));
        SVLogo.setTranslateX(1);
        SVLogo.setTranslateY(1);
		
		this.getChildren().addAll(unidades, camaraText, destinoText, destino, aceptar, SVLogo);
		
		File[] paths;
		paths = File.listRoots();
		for(File path:paths) {
			File checkFile = new File(path + "Device.fit");
			options.add(path.getPath());
			if (checkFile.exists()) {
				unidades.getSelectionModel().select(path.getPath());
				System.out.println(path + " = GARMIN");
			}
		}
	}
}
