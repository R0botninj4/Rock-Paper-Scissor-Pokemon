package rps.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class IntroScreenController {

    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    @FXML
    public void initialize() {

        String videoPath = getClass()
                .getResource("/videos/Intro.mp4")
                .toExternalForm();

        Media media = new Media(videoPath);
        mediaPlayer = new MediaPlayer(media);   // ← now stored

       // mediaPlayer.setVolume(1);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setAutoPlay(true);

        mediaView.setMediaPlayer(mediaPlayer);
    }

    @FXML
    private void handleIntroScreenClick(MouseEvent mouseEvent) {
        try {
            // Stop the video
            mediaView.getMediaPlayer().stop();

            // Load the game view
            Parent root = FXMLLoader.load(
                    getClass().getResource("/rps/gui/view/GameView.fxml")
            );

            // Get the current stage and switch scene
            Stage stage = (Stage) mediaView.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/rps/gui/view/RPS.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}