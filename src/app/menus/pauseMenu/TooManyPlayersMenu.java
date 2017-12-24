package app.menus.pauseMenu;

import app.menus.menu.Menu;
import app.menus.menu.MenuBox;
import app.menus.menu.MenuObject;
import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.Map;

public class TooManyPlayersMenu extends Menu {

  private Map<String, MenuObject> buttons;
  private Rectangle bg;
  private MenuBox menuPause;

  public TooManyPlayersMenu () {
    //TODO: get width and height from App
    bg = new Rectangle(1000, 750);
    bg.setFill(Color.GREY);
    bg.setOpacity(0.9);

    MenuObject pauseOK = new PauseMenuButton("OK", 22);

    Text text = new Text("TOO MANY PLAYERS ALREADY");
    text.setFill(Color.BLACK);
    text.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, 22));

    menuPause = new PauseMenuBox(
        pauseOK
    );
    menuPause.setAlignment(Pos.CENTER);

    VBox menuQuit = new VBox();
    menuQuit.setAlignment(Pos.CENTER);
    menuQuit.getChildren().addAll(text, menuPause);
    setAlignment(Pos.CENTER);

    getChildren().addAll(bg, menuQuit);

    buttons = new HashMap<String, MenuObject>() {{
      put("quitOK", pauseOK);
    }};
  }

  @Override
  public void reload() {
    getChildren().clear();
    getChildren().addAll(bg, menuPause);
  }

  @Override
  public Map<String, MenuObject> getButtonsMap() {
    return buttons;
  }
}
