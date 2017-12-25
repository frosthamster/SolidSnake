package app;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import model.AI.BaseAI;
import model.creatures.snakes.Snake;
import model.game.Game;
import model.game.GameSettings;

public class Settings implements Serializable {

  private int size;
  private int speed;
  private VisualSettings skins; //= new SkinSettings(1, 1, 1);
  private GameSettings gameplaySettings;
  private BiFunction<Game, Snake, BaseAI>[] bots;

  public Settings(int size, int speed, VisualSettings skinSettings, GameSettings gameplaySettings) {
    this.size = size;
    this.speed = speed;
    this.skins = skinSettings;
    this.gameplaySettings = gameplaySettings;
    this.bots = new BiFunction[3];
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public int getCols() {
    return gameplaySettings.getWidth();
  }

  public int getRows() {
    return gameplaySettings.getHeight();
  }

  public int getHeight() {
    return getCols() * size;
  }

  public int getWidth() {
    return getRows() * size;
  }

  public GameSettings getGameplaySettings() {
    return gameplaySettings;
  }

  public void setGameplaySettings(GameSettings gameplaySettings) {
    this.gameplaySettings = gameplaySettings;
  }

  public VisualSettings getSkins() {
    return skins;
  }

  public void setSkins(VisualSettings skins) {
    this.skins = skins;
  }

  public BiFunction<Game, Snake, BaseAI>[] getBots() { return bots; }

  public BiFunction<Game, Snake, BaseAI> getBotAt(int index) { return bots[index]; }

  public void setBots(BiFunction<Game, Snake, BaseAI>[] bots) {
    this.bots = new BiFunction[bots.length];
    System.arraycopy(bots, 0, this.bots, 0, bots.length);
  }
}
