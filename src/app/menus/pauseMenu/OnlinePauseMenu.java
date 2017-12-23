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

public class OnlinePauseMenu extends Menu {

  private Map<String, MenuObject> buttons;
  private Rectangle bg;
  private MenuBox menuPause;

  public OnlinePauseMenu() {
    //TODO: get width and height from App
    bg = new Rectangle(1000, 750);
    bg.setFill(Color.GREY);
    bg.setOpacity(0.9);

    MenuObject pauseResume = new PauseMenuButton("RESUME", 22);
    MenuObject pauseQuit = new PauseMenuButton("QUIT", 22);
    menuPause = new PauseMenuBox(
        pauseResume,
        pauseQuit
    );

    StackPane quitText = new StackPane();
    quitText.setAlignment(Pos.CENTER);
    Text text = new Text("ARE YOU SURE YOU WANT TO QUIT TO MAIN MENU?");
    text.setFill(Color.BLACK);
    text.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, 22));
    quitText.getChildren().add(text);

    HBox quitConfirm = new HBox();
    quitConfirm.setAlignment(Pos.CENTER);
    MenuObject quitYes = new PauseMenuButton("YES", 22);
    MenuObject quitNo = new PauseMenuButton("NO", 22);
    quitConfirm.getChildren().addAll(quitYes, quitNo);

    VBox menuQuit = new VBox();
    menuQuit.setAlignment(Pos.CENTER);
    menuQuit.getChildren().addAll(quitText, quitConfirm);

    pauseResume.setOnMouseClicked(event -> {
    });
    pauseQuit.setOnMouseClicked(event -> {
      getChildren().remove(menuPause);
      getChildren().add(menuQuit);
    });

    quitYes.setOnMouseClicked(event -> {
    });
    quitNo.setOnMouseClicked(event -> {
      getChildren().remove(menuQuit);
      getChildren().add(menuPause);
    });

    menuPause.setAlignment(Pos.CENTER);
    setAlignment(Pos.CENTER);

    getChildren().addAll(bg, menuPause);

    buttons = new HashMap<String, MenuObject>() {{
      put("pauseResume", pauseResume);
      put("quitYes", quitYes);
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
