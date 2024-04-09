package music.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MPlayer extends Application {
    List<File> songQueue = new ArrayList<>();
    int currentSongIndex = -1;
    MediaPlayer mplayer;
    Slider musicSlider;
    Button playPauseButton;
    Button nextButton;
    Button prevButton;

    public MPlayer() {
        musicSlider = new Slider();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        Label filenameLabel = new Label("");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

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

        playPauseButton.setOnAction(e -> {
            if (mplayer != null && mplayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mplayer.pause();
                playPauseButton.setText("Play");
            } else if (mplayer != null && mplayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mplayer.play();
                playPauseButton.setText("Pause");
            }
        });

        nextButton.setOnAction(e -> {
            playNextSong();
        });

        prevButton.setOnAction(e -> {
            playPreviousSong();
        });

        stopButton.setOnAction(e -> {
            if (mplayer != null) {
                mplayer.stop();
            }
        });

        hbox.getChildren().addAll(playPauseButton, prevButton, nextButton, stopButton);
        vbox.getChildren().addAll(filenameLabel, hbox);
        root.setCenter(vbox);

        MenuBar menubar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem selectMenuItem = new MenuItem("Select Folder");

        selectMenuItem.setOnAction(e -> {
            File selectedFolder = directoryChooser.showDialog(primaryStage);
            if (selectedFolder != null) {
                File[] files = selectedFolder.listFiles();
                if (files != null) {
                    songQueue.clear();
                    for (File file : files) {
                        if (file.isFile() && (file.getName().endsWith(".wav") || file.getName().endsWith(".mp3"))) {
                            songQueue.add(file);
                        }
                    }
                    // Sort the songQueue by file names
                    Collections.sort(songQueue, Comparator.comparing(File::getName));
                    // OR Sort the songQueue by artist names if available
                    // Collections.sort(songQueue, Comparator.comparing(this::getArtistName));

                    playNextSong();
                }
            }
        });

        menu.getItems().addAll(selectMenuItem);
        menubar.getMenus().add(menu);
        root.setTop(menubar);

        musicSlider.setOnMouseClicked(event -> {
            if (mplayer != null) {
                mplayer.seek(javafx.util.Duration.seconds(musicSlider.getValue()));
            }
        });

        if (mplayer != null) {
            mplayer.setOnEndOfMedia(this::playNextSong);
        }

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Player");
        primaryStage.show();
    }

    private void playNextSong() {
        if (songQueue.isEmpty())
            return;
        currentSongIndex = (currentSongIndex + 1) % songQueue.size();
        playSong(songQueue.get(currentSongIndex));
    }

    private void playPreviousSong() {
        if (songQueue.isEmpty())
            return;
        currentSongIndex = (currentSongIndex - 1 + songQueue.size()) % songQueue.size();
        playSong(songQueue.get(currentSongIndex));
    }

    private void playSong(File songFile) {
        String songPath = songFile.toURI().toString();
        Media media = new Media(songPath);
        if (mplayer != null) {
            mplayer.stop();
        }
        mplayer = new MediaPlayer(media);
        musicSlider.setMin(0);
        mplayer.setOnReady(() -> {
            musicSlider.setMax(mplayer.getTotalDuration().toSeconds());
            mplayer.play();
            playPauseButton.setText("Pause");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
