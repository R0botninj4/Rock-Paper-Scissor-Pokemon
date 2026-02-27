package rps.gui.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

import rps.bll.game.GameManager;
import rps.bll.game.Move;
import rps.bll.game.Result;
import rps.bll.game.ResultType;
import rps.bll.player.Player;
import rps.bll.player.PlayerType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameViewController implements Initializable {

    // Selection
    @FXML private ImageView pokeball1;
    @FXML private ImageView pokeball2;
    @FXML private ImageView pokeball3;

    // Battle
    @FXML private ImageView playerChoice;
    @FXML private ImageView enemyChoice;
    @FXML private ImageView playerBall;
    @FXML private ImageView enemyBall;

    // HP bars
    @FXML private ImageView playerHP;
    @FXML private ImageView enemyHP;

    // Score labels
    @FXML private Label winText;
    @FXML private Label tieText;
    @FXML private Label lossText;

    private int wins = 0;
    private int losses = 0;
    private int ties = 0;

    private GameManager gameManager;
    private Player human;

    // Pokemon images
    private final Image charmander = new Image("/Images/charmander.png");
    private final Image squirtle   = new Image("/Images/squirtle.png");
    private final Image bulbasaur  = new Image("/Images/bulbasaur.png");

    // Pokemon Back images
    private final Image charmanderBack = new Image("/Images/charmanderBack.png");
    private final Image squirtleBack   = new Image("/Images/squirtleBack.png");
    private final Image bulbasaurBack  = new Image("/Images/bulbasaurBack.png");
    //Music
    private MediaPlayer backgroundMusic;

    // HP images
    private final Image hpFull = new Image("/Images/HPfull.png");
    private final Image hpLow  = new Image("/Images/HPlow.png");

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        human = new Player("Human", PlayerType.Human);
        Player ai = new Player("AI", PlayerType.AI);
        gameManager = new GameManager(human, ai);

        setupPokeball(pokeball1, Move.Rock, charmander);
        setupPokeball(pokeball2, Move.Paper, squirtle);
        setupPokeball(pokeball3, Move.Scissor, bulbasaur);

        updateScoreLabels();

        Media media = new Media(getClass().getResource("/Videos/battle.mp3").toExternalForm());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Looper for evigt
        backgroundMusic.setVolume(0.1); // 0.0 - 1.0
        backgroundMusic.play();
    }

    private void setupPokeball(ImageView ball, Move move, Image hoverImage) {
        addWiggle(ball);
        addImageTooltip(ball, hoverImage); // <-- her tilføjer vi tooltip igen
        ball.setOnMouseClicked(e -> play(move));
    }

    // ================= WIGGLE =================
    private void addWiggle(ImageView imageView) {

        RotateTransition wiggle = new RotateTransition(Duration.millis(100), imageView);
        wiggle.setFromAngle(-15);
        wiggle.setToAngle(15);
        wiggle.setCycleCount(Animation.INDEFINITE);
        wiggle.setAutoReverse(true);

        imageView.setOnMouseEntered(e -> {
            wiggle.play();
            imageView.setCursor(Cursor.HAND);
        });

        imageView.setOnMouseExited(e -> {
            wiggle.stop();
            imageView.setRotate(0);
            imageView.setCursor(Cursor.DEFAULT);
        });
    }

    // ================= TOOLTIP =================
    private void addImageTooltip(ImageView imageView, Image image) {

        Tooltip tooltip = new Tooltip();
        ImageView preview = new ImageView(image);

        preview.setFitWidth(100);
        preview.setPreserveRatio(true);

        tooltip.setGraphic(preview);

        tooltip.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #FFD700;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 8;"
        );

        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);

        imageView.setOnMouseMoved(e -> {
            tooltip.setAnchorX(e.getScreenX() - 60);
            tooltip.setAnchorY(e.getScreenY() - 160);
        });

        Tooltip.install(imageView, tooltip);
    }

    // ================= GAME =================
    private void play(Move humanMove) {

        Image playerImage = switch (humanMove) {
            case Rock -> charmanderBack;
            case Paper -> squirtleBack;
            case Scissor -> bulbasaurBack;
        };

        throwBall(playerBall, playerChoice, playerImage, true);

        Result result = gameManager.playRound(humanMove);

        Move aiMove = result.getLoserMove();
        if (result.getWinnerPlayer() != human) {
            aiMove = result.getWinnerMove();
        }

        Image enemyImage = switch (aiMove) {
            case Rock -> charmander;
            case Paper -> squirtle;
            case Scissor -> bulbasaur;
        };

        throwBall(enemyBall, enemyChoice, enemyImage, false);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.6));
        pause.setOnFinished(e -> updateScore(result));
        pause.play();
    }

    private void throwBall(ImageView ball, ImageView pokemon, Image image, boolean isPlayer) {

        // ===== RESET =====
        ball.setTranslateX(0);
        ball.setTranslateY(0);
        ball.setRotate(0);
        ball.setVisible(true);

        pokemon.setOpacity(0);
        pokemon.setImage(image);
        pokemon.setScaleX(isPlayer ? -1 : 1); // flip for player
        pokemon.setScaleY(1);
        pokemon.setRotate(0); // start rotation nul

        // ===== BALL ANIMATION =====
        TranslateTransition throwForward = new TranslateTransition(Duration.seconds(0.4), ball);
        throwForward.setToX(isPlayer ? 150 : -150);
        throwForward.setToY(isPlayer ? -100 : 100);

        RotateTransition ballSpin = new RotateTransition(Duration.seconds(0.4), ball);
        ballSpin.setByAngle(isPlayer ? 720 : -720); // flere rotationer

        ParallelTransition ballMove = new ParallelTransition(throwForward, ballSpin);

        // ===== POKÉMON POP =====
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), pokemon);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition popUp = new ScaleTransition(Duration.seconds(0.3), pokemon);
        popUp.setFromX(0.5);
        popUp.setFromY(0.5);
        popUp.setToX(1);
        popUp.setToY(1);

        TranslateTransition hop = new TranslateTransition(Duration.seconds(0.2), pokemon);
        hop.setByY(isPlayer ? -20 : 20); // hop op for spiller, ned for AI
        hop.setAutoReverse(true);
        hop.setCycleCount(2);

        RotateTransition wobble = new RotateTransition(Duration.seconds(0.2), pokemon);
        wobble.setFromAngle(-10);
        wobble.setToAngle(10);
        wobble.setCycleCount(2);
        wobble.setAutoReverse(true);

        // reset rotation bagefter så Pokémon står oprejst
        wobble.setOnFinished(e -> pokemon.setRotate(0));

        ParallelTransition pokemonPop = new ParallelTransition(fadeIn, popUp, hop, wobble);

        // ===== SEQUENCE =====
        ballMove.setOnFinished(e -> {
            pokemon.setOpacity(1);    // gør Pokémon synlig
            pokemonPop.play();        // start pop animation
            ball.setVisible(false);   // gem ballen
        });

        ballMove.play();
    }

    // ================= SCORE + HP =================
    private void updateScore(Result result) {

        if (result.getType() == ResultType.Tie) {

            ties++;
            playerHP.setImage(hpLow);
            enemyHP.setImage(hpLow);
        }
        else if (result.getWinnerPlayer() == human) {

            wins++;
            playerHP.setImage(hpFull);
            enemyHP.setImage(hpLow);
        }
        else {

            losses++;
            playerHP.setImage(hpLow);
            enemyHP.setImage(hpFull);
        }

        updateScoreLabels();
    }

    private void updateScoreLabels() {
        winText.setText("W: " + wins);
        tieText.setText("T: " + ties);
        lossText.setText("L: " + losses);
    }
}