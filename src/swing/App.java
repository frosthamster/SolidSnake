package swing;

import lombok.val;
import model.game.Game;
import model.game.GameFrame;
import model.utils.Direction;
import model.utils.Point;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import app.GameplaySettings;

public class App extends JFrame {

  private boolean isGameOver;
  private Direction currDir = null;
  private Game game;
  private GameFrame frame;
  private GameFrame prevFrame = null;
  private boolean isRunning = true;
  private int windowWidth = 800;
  private int windowHeight = 600;
  private int size = 20;
  private int cols = 30;
  private int rows = 30;
  private BufferedImage backBuffer;
  private Insets insets;
  private InputHandler input;

  private int x = 0;

  private static final Map<TextureType, Color> colorDict = new HashMap<TextureType, Color>() {
    {
      put(TextureType.SimpleSnakeBodyPart, Color.CYAN);
      put(TextureType.SnakeHead, Color.BLUE);
      put(TextureType.TailDiscardSnakeBodyPart, Color.MAGENTA);
      put(TextureType.Apple, Color.GREEN);
      put(TextureType.Mushroom, Color.RED);
      put(TextureType.Wall, Color.BLACK);
    }
  };

  public static void main(String[] args) {
    App game = new App();
    game.run();
    System.exit(0);
  }

  private void run() {
    initialize();

    long prevFps = 0;
    long prevUpd = 0;

    while (isRunning) {
      long now = System.currentTimeMillis();

      if ((now - prevFps) >= 16.6) {
        prevFps = now;
        draw();
        handleInput();
        if ((now - prevUpd) >= 200) {
          prevUpd = now;
          if (!isGameOver) {
            frame = game.makeTurn(new Direction[]{currDir});
            if (frame == null) {
              isGameOver = true;
            }
          }
        }
      }
    }

    setVisible(false);
  }

  private void initialize() {
    setTitle("Swing dat Snake");
    setSize(windowWidth, windowHeight);
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);

    insets = getInsets();
    setSize(insets.left + windowWidth + insets.right,
        insets.top + windowHeight + insets.bottom);

    backBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
    input = new InputHandler(this);
    reset(1);
  }

  private void handleInput() {
    if (input.isKeyDown(KeyEvent.VK_W)) {
      currDir = Direction.Up;
    }
    if (input.isKeyDown(KeyEvent.VK_S)) {
      currDir = Direction.Down;
    }
    if (input.isKeyDown(KeyEvent.VK_A)) {
      currDir = Direction.Left;
    }
    if (input.isKeyDown(KeyEvent.VK_D)) {
      currDir = Direction.Right;
    }
    if (input.isKeyDown(KeyEvent.VK_ENTER)) {
      if (isGameOver) {
        reset(1);
      }
    }
  }

  private void draw() {
    Graphics g = getGraphics();
    Graphics bbg = backBuffer.getGraphics();

    paintGame(bbg);

    g.drawImage(backBuffer, insets.left, insets.top, this);
  }

  private void paintGame(Graphics bbg) {
    bbg.setColor(Color.DARK_GRAY);
    bbg.fillRect(0, 0, windowWidth, windowHeight);
    if (frame != null) {
      paintFrame(frame, bbg);
      paintScore(frame, bbg);
      prevFrame = frame;
    } else {
      if (prevFrame == null) {
        throw new IllegalArgumentException("Previous frame was null");
      }
      paintFrame(prevFrame, bbg);
      paintScore(prevFrame, bbg);
      paintResetMessage(bbg);
    }
  }

  private void paintFrame(GameFrame frame, Graphics bbg) {
    frame.getCreaturesInfo().forEach((p, ci) -> {
      bbg.setColor(colorDict.get(CreatureToTextureConverter.converters.get(ci.getType())));
      paintPoint(p, bbg);
    });
  }

  private void paintPoint(Point point, Graphics bbg) {
    bbg.fillRect(point.getX() * size, point.getY() * size, size, size);
  }

  private void paintResetMessage(Graphics bbg) {
    bbg.setColor(Color.WHITE);
    bbg.drawString("Hit ENTER to reset.", 10, windowHeight - 10);
  }

  private void paintScore(GameFrame frame, Graphics bbg) {
    bbg.setColor(Color.WHITE);
    for (int i = 0; i < frame.getScores().length; i++) {
      bbg.drawString("Player " + i + ": " + frame.getScores()[i], 10, 10 + i * 20);
    }
  }

  private void reset(int snakeCount) {
    isGameOver = false;
    currDir = Direction.None;
    GameplaySettings settings = new GameplaySettings(
        GameplaySettings.getRandomField(cols, rows, snakeCount),
        true, 20, 50, 40, 30, snakeCount);
    game = new Game(settings);
    frame = game.makeTurn(new Direction[]{currDir});
  }
}