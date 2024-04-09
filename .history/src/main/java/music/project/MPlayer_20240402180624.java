package music.project;

import javafx.application.Application;
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

public class MPlayer extends Application {
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

    public MPlayer() {
        musicSlider = new Slider();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        currentPlayingLabel = new Label("Currently Playing: ");
        nextSongsLabel = new Label("Next in Queue: ");

        VBox vbox = new VBox(10);
        vbox.getStyleClass().add("background");

        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("controls");

        playPauseButton = new Button("Play");
        nextButton = new Button("Next");
        prevButton = new Button("Previous");
        Button stopButton = new Button("Stop");

        playPauseButton.getStyleClass().add("control-button");
        nextButton.getStyleClass().add("control-button");
        prevButton.getStyleClass().add("control-button");
        stopButton.getStyleClass().add("control-button");

        playPauseButton.setOnAction(e -> togglePlayPause());
        nextButton.setOnAction(e -> playNextSong());
        prevButton.setOnAction(e -> playPreviousSong());
        stopButton.setOnAction(e -> stopPlayback());

        hbox.getChildren().addAll(playPauseButton, prevButton, nextButton, stopButton);

        // Create a slider and bind its properties
        musicSlider = new Slider();
        musicSlider.setMin(0);
        musicSlider.setMax(100);
        musicSlider.setValue(0);
        musicSlider.setShowTickLabels(false);
        musicSlider.setShowTickMarks(true);
        musicSlider.setMajorTickUnit(10);
        musicSlider.setMinorTickCount(5);
        musicSlider.setBlockIncrement(1);
        musicSlider.setOnMouseClicked(event -> seekMedia());

        // Add slider to the VBox
        vbox.getChildren().addAll(currentPlayingLabel, musicSlider, nextSongsLabel, hbox);
        root.setCenter(vbox);

        loadSongsFromFolder();

        mediaPlayer.setOnEndOfMedia(this::playNextSong);

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Player");
        primaryStage.show();
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.setText("Play");
            } else {
                mediaPlayer.play();
                playPauseButton.setText("Pause");
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

            // Set a listener to play the next song when the current song ends
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.stop();
                playNextSong();
            });
        } else {
            // If the song queue is empty, stop playback
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
            playPauseButton.setText("Pause");
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
            playPauseButton.setText("Play");
        }
    }

    private void seekMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(javafx.util.Duration.seconds(musicSlider.getValue()));
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
