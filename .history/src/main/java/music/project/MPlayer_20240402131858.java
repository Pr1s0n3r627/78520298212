package music.project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class MPlayer extends Application {
    File selectedFile;
    MediaPlayer mplayer;
    Slider musicSlider;

    public MPlayer() {
        musicSlider = new Slider();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        Label filenameLabel = new Label("");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Audio Files", "*.wav", "*.mp3"));

        VBox vbox = new VBox(10);
        vbox.getStyleClass().add("background");

        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("controls");

        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");

        playButton.getStyleClass().add("control-button");
        pauseButton.getStyleClass().add("control-button");
        stopButton.getStyleClass().add("control-button");

        playButton.setOnAction(e -> {
            if (mplayer != null) {
                mplayer.play();
            }
        });

        pauseButton.setOnAction(e -> {
            if (mplayer != null) {
                mplayer.pause();
            }
        });

        stopButton.setOnAction(e -> {
            if (mplayer != null) {
                mplayer.stop();
            }
        });

        hbox.getChildren().addAll(playButton, pauseButton, stopButton);
        vbox.getChildren().addAll(filenameLabel, hbox);
        root.setCenter(vbox);

        MenuBar menubar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem selectMenuItem = new MenuItem("Select");

        selectMenuItem.setOnAction(e -> {
            selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                String songPath = selectedFile.toURI().toString();
                Media media = new Media(songPath);
                if (mplayer != null) {
                    mplayer.stop();
                }
                mplayer = new MediaPlayer(media);
                musicSlider.setMin(0);
                musicSlider.setMax(mplayer.getTotalDuration().toSeconds());
                mplayer.play();
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
            mplayer.setOnEndOfMedia(() -> {
                mplayer.stop();
            });
        }

        Scene scene = new Scene(root, 400, 200);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Player");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
