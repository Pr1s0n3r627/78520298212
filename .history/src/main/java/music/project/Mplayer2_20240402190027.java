package music.project;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Slider seekSlider;
    private Map<File, Metadata> metadata = new HashMap<>();

    public MPlayer2() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Load default album cover image
        Image defaultAlbumCover = new Image(getClass().getResourceAsStream("default_album_cover.jpg"));
        albumCoverView = new ImageView(defaultAlbumCover);
        albumCoverView.setFitWidth(300);
        albumCoverView.setFitHeight(300);
        albumCoverView.setPreserveRatio(true);
        root.setCenter(albumCoverView);

        titleLabel = new Label("");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        artistLabel = new Label("");
        artistLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #B3B3B3;");

        VBox bottomControls = createBottomControls();
        root.setBottom(bottomControls);

        // Create control buttons
        HBox controlButtonsBox = new HBox(20);
        controlButtonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        playPauseButton = new Button();
        playPauseButton.getStyleClass().add("control-button");
        playPauseButton.setPrefSize(60, 60);
        playPauseButton.setOnAction(e -> togglePlayPause());

        nextButton = new Button();
        nextButton.getStyleClass().add("control-button");
        nextButton.setPrefSize(60, 60);
        nextButton.setOnAction(e -> playNextSong());

        prevButton = new Button();
        prevButton.getStyleClass().add("control-button");
        prevButton.setPrefSize(60, 60);
        prevButton.setOnAction(e -> playPreviousSong());

        controlButtonsBox.getChildren().addAll(prevButton, playPauseButton, nextButton);
        root.setTop(controlButtonsBox);

        loadSongsFromFolder();

        mediaPlayer.setOnEndOfMedia(this::playNextSong);

        Scene scene = new Scene(root, 450, 800); // Set window size to 450 x 800
        scene.getStylesheets().add(getClass().getResource("/music/project/styles.css").toExternalForm()); // Load CSS
        primaryStage.setScene(scene);
        primaryStage.setTitle("MPlayer2");
        primaryStage.show();
    }

    private VBox createBottomControls() {
        VBox bottomControls = new VBox(10);
        bottomControls.setAlignment(javafx.geometry.Pos.CENTER);

        VBox songInfoBox = new VBox(5);
        songInfoBox.setAlignment(javafx.geometry.Pos.CENTER);
        songInfoBox.getChildren().addAll(titleLabel, artistLabel);

        seekSlider = new Slider();
        seekSlider.getStyleClass().add("seek-slider");
        seekSlider.setMin(0);
        seekSlider.setMax(100);
        seekSlider.setValue(0);
        seekSlider.setPrefWidth(300);
        seekSlider.setOnMousePressed(e -> seekMedia());
        seekSlider.setOnMouseDragged(e -> seekMedia());

        HBox controlButtonsBox = new HBox(20);
        controlButtonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        controlButtonsBox.getChildren().addAll(prevButton, playPauseButton, nextButton);

        bottomControls.getChildren().addAll(songInfoBox, controlButtonsBox, seekSlider);
        return bottomControls;
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseButton.getStyleClass().remove("pause-button");
                playPauseButton.getStyleClass().add("play-button");
            } else {
                mediaPlayer.play();
                playPauseButton.getStyleClass().remove("play-button");
                playPauseButton.getStyleClass().add("pause-button");
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
                        updateMetadata(file);
                    }
                }
                Collections.sort(songQueue, Comparator.comparing(File::getName));
                playNextSong();
            }
        }
    }

    private void updateMetadata(File file) {
        // You need to implement metadata extraction from the file
    }

    private void playNextSong() {
        // Implement playing the next song
    }

    private void playPreviousSong() {
        // Implement playing the previous song
    }

    private void playSong(File songFile) {
        // Implement playing a specific song
    }

    private void stopPlayback() {
        // Implement stopping the playback
    }

    private void seekMedia() {
        // Implement seeking the media
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Define a class to hold metadata
    private static class Metadata {
        private final String title;
        private final String artist;

        public Metadata(String title, String artist) {
            this.title = title;
            this.artist = artist;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }
    }
}
