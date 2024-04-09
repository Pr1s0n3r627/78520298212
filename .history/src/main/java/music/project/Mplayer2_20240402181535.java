import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MPlayer2 extends Application {
    private final String songsFolderPath = "songs";
    private List<File> songQueue = new ArrayList<>();
    private int currentSongIndex = -1;
    private MediaPlayer mediaPlayer;
    private Slider musicSlider;
    private Button playPauseButton;
    private Button nextButton;
    private Button prevButton;
    private Label currentPlayingLabel;
    private Label nextSongsLabel;

    public MPlayer2() {
        musicSlider = new Slider();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        currentPlayingLabel = new Label();
        nextSongsLabel = new Label();

        VBox controlsBox = new VBox(10);
        controlsBox.setPadding(new Insets(10));
        controlsBox.getStyleClass().add("background");

        HBox hbox = new HBox(10);

        playPauseButton = new Button("PLAY");
        playPauseButton.getStyleClass().add("control-button");
        playPauseButton.setOnAction(e -> togglePlayPause());

        nextButton = new Button(">>");
        nextButton.getStyleClass().add("control-button");
        nextButton.setOnAction(e -> playNextSong());

        prevButton = new Button("<<");
        prevButton.getStyleClass().add("control-button");
        prevButton.setOnAction(e -> playPreviousSong());

        hbox.getChildren().addAll(prevButton, playPauseButton, nextButton);
        hbox.getStyleClass().add("controls");

        musicSlider.getStyleClass().add("custom-slider");

        controlsBox.getChildren().addAll(currentPlayingLabel, musicSlider, nextSongsLabel, hbox);
        root.setCenter(controlsBox);

        loadSongsFromFolder();

        mediaPlayer.setOnEndOfMedia(this::playNextSong);

        Scene scene = new Scene(root, 400, 200);
        scene.getStylesheets().add(getClass().getResource("/music/project/styles.css").toExternalForm()); // Load CSS
        primaryStage.setScene(scene);
        primaryStage.setTitle("MPlayer2");
        primaryStage.show();
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.setText("PLAY");
            } else {
                mediaPlayer.play();
                playPauseButton.setText("PAUSE");
            }
        }
    }

    private void loadSongsFromFolder() {
        File folder = new File(songsFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (file.getName().endsWith(".wav") || file.getName().endsWith(".mp3"))) {
                        songQueue.add(file);
                    }
                }
                Collections.sort(songQueue, Comparator.comparing(File::getName));
                playNextSong();
            }
        }
    }

    private void playNextSong() {
        if (!songQueue.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % songQueue.size();
            updateLabels();
            playSong(songQueue.get(currentSongIndex));

            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                playNextSong();
            });
        } else {
            stopPlayback();
        }
    }

    private void playPreviousSong() {
        if (!songQueue.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + songQueue.size()) % songQueue.size();
            updateLabels();
            playSong(songQueue.get(currentSongIndex));
        }
    }

    private void playSong(File songFile) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        String songPath = songFile.toURI().toString();
        Media media = new Media(songPath);
        mediaPlayer = new MediaPlayer(media);
        musicSlider.setMin(0);

        mediaPlayer.setOnReady(() -> {
            musicSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            mediaPlayer.play();
            playPauseButton.setText("PAUSE");
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!musicSlider.isValueChanging()) {
                musicSlider.setValue(newValue.toSeconds());
            }
        });
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playPauseButton.setText("PLAY");
        }
    }

    private void updateLabels() {
        if (currentSongIndex >= 0 && currentSongIndex < songQueue.size()) {
            currentPlayingLabel.setText("Currently Playing: " + songQueue.get(currentSongIndex).getName());
        }
        StringBuilder nextSongs = new StringBuilder();
        for (int i = currentSongIndex + 1; i < Math.min(currentSongIndex + 4, songQueue.size()); i++) {
            nextSongs.append(songQueue.get(i).getName()).append("\n");
        }
        nextSongsLabel.setText("Next in Queue:\n" + nextSongs.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
