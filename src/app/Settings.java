package app;

import java.io.Serializable;
import model.game.GameSettings;

public class Settings implements Serializable {

  private int size;
  private int speed;
  private VisualSettings skins; //= new SkinSettings(1, 1, 1);
  private GameSettings gameplaySettings;
  private boolean[] bots;

  public Settings(int size, int speed, VisualSettings skinSettings, GameSettings gameplaySettings) {
    this.size = size;
    this.speed = speed;
    this.skins = skinSettings;
    this.gameplaySettings = gameplaySettings;
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

  public boolean[] getBots() { return bots; }

  public void setBots(boolean[] bots) {
    this.bots = new boolean[bots.length];
    System.arraycopy(bots, 0, this.bots, 0, bots.length);
  }
}
