package app;

import app.drawing.TextureType;
import java.io.Serializable;
import java.util.HashMap;
import javafx.scene.image.Image;
import java.util.Map;

import static app.drawing.TextureType.*;

public class SkinSettings implements VisualSettings {

  private final int skinVariations = 2;
  private final Map<TextureType, Image> spritesForSubjects;

  private Map<Integer, Map<TextureType, Image>> sprites;
  private int player1Skin;
  private int player2Skin;
  private int player3Skin;

  public SkinSettings(int player1Skin, int player2Skin, int player3Skin) {
    this.player1Skin = player1Skin;
    this.player2Skin = player2Skin;
    this.player3Skin = player3Skin;

    changePlayerSkins();

    spritesForSubjects = new HashMap<TextureType, Image>() {{
      put(Apple, new Image("images/Apple.png"));
      put(Mushroom, new Image("images/Mushroom.png"));
      put(Wall, new Image("images/Wall.png"));
    }};
  }

  @Override
  public Map<Integer, Map<TextureType, Image>> getSpritesForPlayers() {
    return sprites;
  }

  @Override
  public Map<TextureType, Image> getSpritesForSubjects() {
    return spritesForSubjects;
  }

  public int getSkinVariations() {
    return skinVariations;
  }

  public void setSkins(int player1skin, int player2skin, int player3skin) {
    this.player1Skin = player1skin;
    this.player2Skin = player2skin;
    this.player3Skin = player3skin;
    changePlayerSkins();
  }

  //TODO: maybe do an array??? (good for GameScreenUI)
  public int getPlayer1Skin() {
    return player1Skin;
  }

  public int getPlayer2Skin() {
    return player2Skin;
  }

  public int getPlayer3Skin() {
    return player3Skin;
  }

  private void changePlayerSkins() {
    sprites = new HashMap<Integer, Map<TextureType, Image>>() {
      {
        put(0, new HashMap<TextureType, Image>() {{
              put(SnakeHead, new Image(String.format("images/Skin%d/Head1.png", player1Skin)));
              put(TailDiscardSnakeBodyPart,
                  new Image(String.format("images/Skin%d/TailDiscard.png", player1Skin)));
              put(SimpleSnakeBodyPart,
                  new Image(String.format("images/Skin%d/Simple.png", player1Skin)));
            }}
        );
        put(1, new HashMap<TextureType, Image>() {
          {
            put(SnakeHead, new Image(String.format("images/Skin%d/Head2.png", player2Skin)));
            put(TailDiscardSnakeBodyPart,
                new Image(String.format("images/Skin%d/TailDiscard.png", player2Skin)));
            put(SimpleSnakeBodyPart, new Image(
                String.format("images/Skin%d/Simple.png", player2Skin)));
          }
        });
        put(2, new HashMap<TextureType, Image>() {
          {
            put(SnakeHead, new Image(String.format("images/Skin%d/Head3.png", player3Skin)));
            put(TailDiscardSnakeBodyPart,
                new Image(String.format("images/Skin%d/TailDiscard.png", player3Skin)));
            put(SimpleSnakeBodyPart, new Image(
                String.format("images/Skin%d/Simple.png", player3Skin)));
          }
        });
      }
    };
  }
}
