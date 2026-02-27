package rps.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class JavaFXApp extends Application {

    public static void launch() {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Load FXML
        Parent root = FXMLLoader.load(
                getClass().getResource("/rps/gui/view/IntroScreen.fxml")
        );

        // Load custom font (must be inside src/Fonts/)
        Font font = Font.loadFont(
                getClass().getResource("/Fonts/ARCADECLASSIC.ttf").toExternalForm(),
                14
        );

        // Debug print (safe even if font fails)
        System.out.println("Loaded font: " + font);
        if (font != null) {
            System.out.println("Font name: " + font.getName());
        }

        // Create scene
        Scene scene = new Scene(root);

        // Load CSS
        scene.getStylesheets().add(
                getClass().getResource("/rps/gui/view/RPS.css").toExternalForm()
        );

        stage.setTitle("Rock Paper Scissor");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}