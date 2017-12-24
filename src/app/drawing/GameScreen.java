package app.drawing;

import static model.utils.Direction.None;
import static model.utils.Direction.Up;

import app.App;
import app.Settings;
import app.drawing.GameScreenUI.PlayersBar;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import model.creatures.CreatureType;
import model.creatures.CreatureTypeValidator;
import model.game.GameFrame;
import model.utils.Direction;

@SuppressWarnings("Duplicates")
public class GameScreen extends StackPane {

  private Settings settings;
  private final int canvasWidth;
  private final int canvasHeight;
  private GraphicsContext gc;
  private PlayersBar playersBar;
  private StackPane gameOverScreen;
  private Text winner;

  private GameFrame prevFrame = null;

  public GameScreen(Settings settings) {
    this.settings = settings;

    canvasWidth = settings.getWidth();
    canvasHeight = settings.getHeight();
    Canvas canvas = new Canvas(this.canvasWidth, this.canvasHeight);
    gc = canvas.getGraphicsContext2D();

    StackPane canvasPane = new StackPane();
    canvasPane.setAlignment(Pos.CENTER);

    VBox root = new VBox();
    playersBar = new PlayersBar(settings);
    playersBar.setAlignment(Pos.BOTTOM_CENTER);

    // Don't touch this
//        gameOverScreen = new StackPane();
//        gameOverScreen.setAlignment(Pos.CENTER);
//        gameOverScreen.setPrefHeight(200);
//        VBox textBox = new VBox();
//        Text gameOver = new Text("OPERATION IS OVER");
//        gameOver.setFont(new Font(40));
//        winner = new Text();
//        winner.setFont(new Font(35));
//        textBox.getChildren().addAll(gameOver, winner);
//        gameOverScreen.getChildren().addAll(textBox);

    canvasPane.getChildren().addAll(canvas);
    root.getChildren().addAll(canvasPane, playersBar);
    getChildren().add(root);
  }

  public void update(GameFrame frame) {
    //TODO: Set custom background
    gc.setFill(Color.DARKGRAY);
    gc.fillRect(0, 0, canvasWidth, canvasHeight);

    if (frame != null) {
      paintFrame(frame);
      playersBar.update(frame.getScores());
      prevFrame = frame;
    } else {
      if (prevFrame == null) {
        throw new IllegalArgumentException("Previous frame was null");
      }
      playersBar.update(prevFrame.getScores());

      int highest = -1;
      int winner = -1;
      for (int i = 0; i < prevFrame.getScores().length; i++) {
        if (prevFrame.getScores()[i] > highest) {
          highest = prevFrame.getScores()[i];
          winner = i + 1;
        }
      }
//            winner.setText(String.format(""));
//            gameOverScreen.setVisible(true);
      gc.setTextAlign(TextAlignment.CENTER);
      paintResetMessage(winner, highest);
    }
  }

  private void paintFrame(GameFrame frame) {
    frame.getCreaturesInfo().forEach((p, ci) -> {
      if (CreatureTypeValidator.isSnake(ci.getType())) {
        Image image = settings
            .getSkins()
            .getSpritesForPlayers()
            .get(ci.getPlayerNumber())
            .get(CreatureToTextureConverter.converters.get(ci.getType()));
//                Image rotatedImage = rotateImage(image, ci);
//                gc.drawImage(
//                        rotatedImage,
//                        p.getX() * settings.getSize(),
//                        p.getY() * settings.getSize(),
//                        settings.getSize(),
//                        settings.getSize()
//                );
        double angle = ci.getType() == CreatureType.SnakeHead
            ? 0
            : getAngleFromDirection(ci.getDirection());
        drawRotatedImage(
            gc, image, angle,
            p.getX() * settings.getSize(),
            p.getY() * settings.getSize(),
            settings.getSize(),
            settings.getSize()
        );
      } else {
        gc.drawImage(
            settings.getSkins()
                .getSpritesForSubjects()
                .get(CreatureToTextureConverter.converters.get(ci.getType())),
            p.getX() * settings.getSize(),
            p.getY() * settings.getSize(),
            settings.getSize(),
            settings.getSize()
        );
      }
    });
  }

  // Don't like this implementation coz of mixing different APIs
  private Image rotateImage(Image image, GameFrame.CreatureInfo creatureInfo) {
    if (creatureInfo.getType() == CreatureType.SnakeHead
        || creatureInfo.getDirection() == Up
        || creatureInfo.getDirection() == None) {
      return image;
    }

    ImageView iv = new ImageView(image);
    iv.setRotate(getAngleFromDirection(creatureInfo.getDirection()));
    SnapshotParameters params = new SnapshotParameters();
    params.setFill(Color.TRANSPARENT);
    return iv.snapshot(params, null);
  }

  private double getAngleFromDirection(Direction direction) {
    switch (direction) {
      case None:
      case Up:
        return 0;
      case Right:
        return 90;
      case Down:
        return 180;
      case Left:
        return 270;
    }
    return 0;
  }

  private void rotate(GraphicsContext gc, double angle, double px, double py) {
    Rotate r = new Rotate(angle, px, py);
    gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
  }

  private void drawRotatedImage(
      GraphicsContext gc,
      Image image,
      double angle,
      double x,
      double y,
      double width,
      double height) {
    gc.save();
    rotate(gc, angle, x + width / 2, y + height / 2);
    gc.drawImage(image, x, y, width, height);
    gc.restore();
  }

  private void paintResetMessage(int winner, int highest) {
    gc.setFont(new Font(30));
    gc.setFill(Color.RED);
    gc.fillText("OVER", settings.getWidth() / 2, settings.getHeight() / 2 - 15);

    gc.setFont(new Font(20));
    gc.setFill(Color.BLACK);
    gc.fillText("OPERATION IS", settings.getWidth() / 2, settings.getHeight() / 2 - 30);

    gc.setFont(new Font(20));
    gc.setFill(Color.BLACK);
    gc.fillText("WON WITH", settings.getWidth() / 2, settings.getHeight() / 2 + 50);

    String str = "NOBODY";
    switch (winner) {
      case 1:
        str = "BLUE SNAKE";
        gc.setFill(Color.BLUE);
        break;
      case 2:
        str = "RED SNAKE";
        gc.setFill(Color.RED);
        break;
      case 3:
        str = "TOXIC SNAKE";
        gc.setFill(Color.GREEN);
        break;
    }
    gc.setFont(new Font(30));
    gc.fillText(str, settings.getWidth() / 2, settings.getHeight() / 2 + 30);

    gc.setFont(new Font(30));
    gc.setFill(Color.RED);
    gc.fillText(String.format("%d", highest), settings.getWidth() / 2,
        settings.getHeight() / 2 + 80);

    gc.setFont(new Font(20));
    gc.setFill(Color.BLACK);
    if (!App.getCurrentlyOnLine()) {
      gc.fillText("(press ENTER to start the operation again)", settings.getWidth() / 2,
          settings.getHeight() / 2 + 120);
    }
  }
}
