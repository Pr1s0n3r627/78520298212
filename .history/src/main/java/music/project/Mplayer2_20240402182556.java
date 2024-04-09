package music.project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ImageView albumCoverView;
    private Label titleLabel;
    private Label artistLabel;
    private Button playPauseButton;
    private Button nextButton;
    private Button prevButton;

    public MPlayer2() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Load default album cover image
        Image defaultAlbumCover = new Image(getClass().getResourceAsStream("default_album_cover.jpg"));
        albumCoverView = new ImageView(defaultAlbumCover);
        albumCoverView.setFitWidth(400);
        albumCoverView.setFitHeight(225);
        albumCoverView.setPreserveRatio(true);
        root.setCenter(albumCoverView);

        titleLabel = new Label("");
        titleLabel.setStyle("-fx-font-size: 20px;");
        artistLabel = new Label("");
        artistLabel.setStyle("-fx-font-size: 18px;");

        VBox songInfoBox = new VBox(10);
        songInfoBox.getChildren().addAll(titleLabel, artistLabel);
        songInfoBox.setPadding(new Insets(20, 0, 0, 0));
        songInfoBox.setAlignment(javafx.geometry.Pos.CENTER);
        root.setBottom(songInfoBox);

        // Create control buttons
        HBox controlButtonsBox = new HBox(20);
        controlButtonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        playPauseButton = new Button("PLAY");
        playPauseButton.setPrefSize(60, 60);
        playPauseButton.setOnAction(e -> togglePlayPause());

        nextButton = new Button(">>");
        nextButton.setPrefSize(60, 60);
        nextButton.setOnAction(e -> playNextSong());

        prevButton = new Button("<<");
        prevButton.setPrefSize(60, 60);
        prevButton.setOnAction(e -> playPreviousSong());

        controlButtonsBox.getChildren().addAll(prevButton, playPauseButton, nextButton);
        root.setRight(controlButtonsBox);

        loadSongsFromFolder();

        mediaPlayer.setOnEndOfMedia(this::playNextSong);

        Scene scene = new Scene(root, 800, 450); // 16:9 ratio
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
            updateSongInfo();
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
            updateSongInfo();
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

        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
            playPauseButton.setText("PAUSE");
        });
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            playPauseButton.setText("PLAY");
        }
    }

    private void updateSongInfo() {
        if (currentSongIndex >= 0 && currentSongIndex < songQueue.size()) {
            String songName = songQueue.get(currentSongIndex).getName();
            titleLabel.setText(songName.substring(0, songName.lastIndexOf("."))); // Remove file extension
            artistLabel.setText("Artist Name");
            // Load album cover image if available
            // For simplicity, here we just use a default album cover image
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
