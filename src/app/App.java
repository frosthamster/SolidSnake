package app;

import app.drawing.GameScreen;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import app.menus.menu.Menu;
import app.menus.menu.MenuObject;
import app.menus.mainMenu.MainMenu;
import app.menus.pauseMenu.PauseMenu;
import model.utils.Direction;
import model.game.Game;
import model.game.GameFrame;
import java.util.Map;

public class App extends Application {

  private static Game game;
  private static GameFrame frame;
  private static boolean isGameOver;
  private static boolean isPaused = false;
  private static Direction[] currDir;
  private static int snakeCount = 1;

  // This values are initital and are being used to set default settings
  // (but might be used as properties later, I dunno)
  private static int cellSize = 30;
  private static int speed = 150;

  private static Stage theStage;
  private static AnimationTimer gameLoop;
  private static Settings settings;
  private static int width = 800;
  private static int height = 600;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Snake Reborn");
    primaryStage.setResizable(false);
    primaryStage.setFullScreen(false);
    primaryStage.setOnCloseRequest(e -> System.exit(0));

    settings = new Settings(
        cellSize,
        speed,
        new SkinSettings(0, 0, 0),
        new GameplaySettings(
            GameplaySettings.getRandomField(
                width / cellSize,
                (height - 150) / cellSize,
                snakeCount),
            true,
            20,
            50,
            40,
            30,
            snakeCount
        )
    );
    theStage = primaryStage;
    theStage.setScene(new Scene(createMainMenu(), Color.BLACK));
    theStage.show();
  }

  private void playSnake(int snakeCount) {
    App.snakeCount = snakeCount;
    reset(App.snakeCount);
    Parent gamePlay = createGamePlay();
    theStage.setScene(new Scene(gamePlay, Color.BLACK));
    gamePlay.requestFocus();
    gameLoop.start();
  }

  private Parent createGamePlay() {
    StackPane root = new StackPane();
    root.setPrefSize(width, height);
    root.setBackground(new Background(
        new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)
    ));

    Menu pauseMenu = new PauseMenu();
    Map<String, MenuObject> bm = pauseMenu.getButtonsMap();
    bm.get("pauseResume").setOnMouseClicked(event -> {
      isPaused = false;
      root.getChildren().remove(pauseMenu);
      pauseMenu.reload();
    });
    bm.get("pauseRestart").setOnMouseClicked(event -> {
      isPaused = false;
      root.getChildren().remove(pauseMenu);
      pauseMenu.reload();
      reset(snakeCount);
    });
    bm.get("quitYes").setOnMouseClicked(event -> {
      isPaused = false;
      gameLoop.stop();
      FadeTransition fade = new FadeTransition(Duration.millis(300), root);
      fade.setFromValue(1);
      fade.setToValue(0);
      fade.setOnFinished(e -> theStage.setScene(new Scene(createMainMenu(), Color.BLACK)));
      fade.play();
    });

    GameScreen gameScreen = new GameScreen(settings);
    root.getChildren().add(gameScreen);

    root.setOnKeyPressed(e -> {
      switch (e.getCode()) {
        case W:
          currDir[0] = Direction.Up;
          break;
        case S:
          currDir[0] = Direction.Down;
          break;
        case A:
          currDir[0] = Direction.Left;
          break;
        case D:
          currDir[0] = Direction.Right;
          break;
        case UP:
          currDir[1] = Direction.Up;
          break;
        case DOWN:
          currDir[1] = Direction.Down;
          break;
        case LEFT:
          currDir[1] = Direction.Left;
          break;
        case RIGHT:
          currDir[1] = Direction.Right;
          break;
        case NUMPAD8:
          currDir[2] = Direction.Up;
          break;
        case NUMPAD5:
          currDir[2] = Direction.Down;
          break;
        case NUMPAD4:
          currDir[2] = Direction.Left;
          break;
        case NUMPAD6:
          currDir[2] = Direction.Right;
          break;
        case ENTER:
          if (isGameOver) {
            reset(snakeCount);
          }
          break;
        case ESCAPE:
          if (isPaused) {
            isPaused = false;
            root.getChildren().remove(pauseMenu);
            pauseMenu.reload();
          } else {
            isPaused = true;
            root.getChildren().add(pauseMenu);
          }
      }
    });

    gameLoop = new AnimationTimer() {

      private long prevTime = 0;

        gameLoop = new AnimationTimer(){
            private long prevTime = 0;

            @Override
            public void handle(long now) {
                if (!isGameOver && !isPaused) {
                    if ((now - prevTime) >= settings.getSpeed() * 1000000) {
                        prevTime = now;
                        Direction[] directions = new Direction[snakeCount];
                        System.arraycopy(currDir, 0, directions, 0, snakeCount);
                        frame = game.makeTurn(directions);
                        if (frame == null) {
                            isGameOver = true;
                        }
                    }
                }
                gameScreen.update(frame);
            }
          }
        }
        gameScreen.update(frame);
      }
    };

    return root;
  }

  private Parent createMainMenu() {
    StackPane root = new StackPane();
    root.setPrefSize(width, height);
    root.setBackground(
        new Background(
            new BackgroundFill(
                Color.BLACK,
                CornerRadii.EMPTY,
                Insets.EMPTY)
        )
    );

    ImageView snakeLogo = new ImageView(
        new Image("images/snakeLogoHD.png",
            600,
            0,
            true,
            true)
    );
    FadeTransition logoFade = new FadeTransition(Duration.millis(1000), snakeLogo);
    logoFade.setFromValue(0);
    logoFade.setToValue(1);

    snakeLogo.setOpacity(0);
    root.getChildren().add(snakeLogo);
    StackPane.setAlignment(snakeLogo, Pos.TOP_CENTER);

    // We need to specify a link to skinSettings
    //TODO: MainMenu has logic of skin setting/changing, is it bad?
    // I think the logic should be done in this method, but who cares anyway?
    //TODO: polymorphism is redundant!? (it makes everything harder)
    // Why would you even have several implementations of VisSettings
    // and if so, what would they do?
    Menu mainMenu = new MainMenu(settings);
    Map<String, MenuObject> mb = mainMenu.getButtonsMap();
    mb.get("playSolo").setOnMouseClicked(event -> {
      if (event.getClickCount() < 2) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), root);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> playSnake(1));
        fade.play();
      }
    });
    mb.get("playDuo").setOnMouseClicked(event -> {
      if (event.getClickCount() < 2) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), root);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> playSnake(2));
        fade.play();
      }
    });
    mb.get("playTrio").setOnMouseClicked(event -> {
      if (event.getClickCount() < 2) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), root);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> playSnake(3));
        fade.play();
      }
    });

    FadeTransition startFade = new FadeTransition(Duration.millis(1000), mainMenu);
    startFade.setFromValue(0);
    startFade.setToValue(1);
    logoFade.setOnFinished(event -> {
      startFade.play();
      mainMenu.setOpacity(0);
      root.getChildren().add(mainMenu);
    });
    logoFade.play();

    return root;
  }

  private static void reset(int snakeCount) {
    isGameOver = false;
    currDir = new Direction[3];
    for (int i = 0; i < currDir.length; i++) {
      currDir[i] = Direction.None;
    }
    settings.setGameplaySettings(
        new GameplaySettings(
            GameplaySettings.getRandomField(
                width / settings.getSize(),
                (height - 120) / settings.getSize(),
                snakeCount),
            true,
            20,
            50,
            40,
            30,
            snakeCount
        )
    );
    game = new Game(settings.getGameplaySettings());
    Direction[] directions = new Direction[snakeCount];
    System.arraycopy(currDir, 0, directions, 0, snakeCount);
    frame = game.makeTurn(directions);
  }
}
