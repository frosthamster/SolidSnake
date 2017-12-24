package app.menus.mainMenu;

import app.Settings;
import app.SkinSettings;
import app.menus.mainMenu.skinMenuBox.SkinMenuBox;
import app.menus.menu.Menu;
import app.menus.menu.MenuBox;
import app.menus.menu.MenuObject;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.Map;

public class MainMenu extends Menu {

  private Map<String, MenuObject> buttons;
  private VBox menuWithInfo;
  private StackPane startPane;
  private MainMenuInfoText infoText;
  private MainMenuEdit connectEdit;
  private boolean bots[];

  public MainMenu(Settings settings) {
    menuWithInfo = new VBox();
    menuWithInfo.setAlignment(Pos.BOTTOM_CENTER);

    startPane = new StackPane();
    infoText = new MainMenuInfoText("");

    MenuObject mainPlay = new MainMenuButton("PLAY");
    MenuObject mainOptions = new MainMenuButton("OPTIONS");
    MenuObject mainExit = new MainMenuButton("EXIT");
    MenuBox menuMain = new MainMenuBox(
        mainPlay,
        mainOptions,
        mainExit
    );

    MainMenuSlider optionsSpeed = new MainMenuSlider(1, 20, 21 - settings.getSpeed() / 50,
        "GAME SPEED");
    MainMenuSlider optionsSize = new MainMenuSlider(10, 60, settings.getSize(),
        "SIZE OF GAME OBJECTS");
    MenuObject optionsSkins = new MainMenuButton("SKINS");
    MenuObject optionsBots = new MainMenuButton("BOTS");
    MenuObject optionsBack = new MainMenuButton("BACK");
    MenuBox menuOptions = new MainMenuBox(
        optionsSpeed,
        optionsSize,
        optionsSkins,
        optionsBots,
        optionsBack
    );

    MenuBox menuSkins = new SkinMenuBox((SkinSettings) settings.getSkins());

    bots = new boolean[3];
    MenuObject[] optionsPlayerBots = new MenuObject[3];
    for (int i = 0; i < 3; ++i) {
      optionsPlayerBots[i] = new MainMenuButton(String.format("PLAYER %d: NOT BOT", i + 1));
      int j = i;
      optionsPlayerBots[i].setOnMouseClicked(event -> {
        bots[j] = !bots[j];
        ((MainMenuButton)optionsPlayerBots[j]).setText(String.format(
            "PLAYER %d: %s", j + 1, bots[j] ? "BOT" : "NOT BOT"));
        settings.setBots(bots);
      });
    }
    MenuObject optionsBotsBack = new MainMenuButton("BACK");
    MenuBox menuBots = new MainMenuBox(
        optionsPlayerBots[0],
        optionsPlayerBots[1],
        optionsPlayerBots[2],
        optionsBotsBack
    );

    MenuObject playSolo = new MainMenuButton("SOLO");
    MenuObject playDuo = new MainMenuButton("DUO");
    MenuObject playTrio = new MainMenuButton("TRIO");
    MenuObject playOnline = new MainMenuButton("ONLINE");
    MenuObject playBack = new MainMenuButton("BACK");
    MenuBox menuPlay = new MainMenuBox(
        playSolo,
        playDuo,
        playTrio,
        playOnline,
        playBack
    );

    MenuObject connectCreateDuo = new MainMenuButton("CREATE DUO");
    MenuObject connectCreateTrio = new MainMenuButton("CREATE TRIO");
    connectEdit = new MainMenuEdit("SERVER IP");
    MenuObject connectBack = new MainMenuButton("BACK");
    MenuObject connectPlay = new MainMenuButton("CONNECT");
    MenuBox menuConnect = new MainMenuBox(
        connectCreateDuo,
        connectCreateTrio,
        connectEdit,
        connectPlay,
        connectBack
    );

    mainPlay.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuMain, menuPlay);
      infoText.setText("");
    });
    mainOptions.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuMain, menuOptions);
      infoText.setText("");
    });
    mainExit.setOnMouseClicked(event -> System.exit(0));

    optionsSpeed.getSlider().setBlockIncrement(1);
    optionsSpeed.getSlider().setMajorTickUnit(1);
    optionsSpeed.getSlider().setMinorTickCount(0);
    optionsSpeed.getSlider().setShowTickLabels(true);
    optionsSpeed.getSlider().setSnapToTicks(true);
    optionsSpeed.getSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
      settings.setSpeed(newValue.intValue() * 50);
      settings.setSpeed((21 - newValue.intValue()) * 50);
    });
    optionsSize.getSlider().setBlockIncrement(5);
    optionsSize.getSlider().setMajorTickUnit(10);
    optionsSize.getSlider().setMinorTickCount(4);
    optionsSize.getSlider().setShowTickLabels(true);
    optionsSize.getSlider().setShowTickMarks(true);
    optionsSize.getSlider().setSnapToTicks(true);
    optionsSize.getSlider().valueProperty().addListener((observable, oldValue, newValue) ->
        settings.setSize(newValue.intValue()));
    optionsSkins.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuOptions, menuSkins);
      infoText.setText("");
    });
    optionsBack.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuOptions, menuMain);
      infoText.setText("");
    });

    optionsBots.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuOptions, menuBots);
      infoText.setText("");
    });
    optionsBotsBack.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuBots, menuOptions);
      infoText.setText("");
    });

    Map<String, MenuObject> skinButtons = menuSkins.getButtonsMap();
    skinButtons.get("skinAccept").setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuSkins, menuOptions);
      infoText.setText("");
    });
      skinButtons.get("skinDecline").setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuSkins, menuOptions);
      infoText.setText("");
    });

    playOnline.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuPlay, menuConnect);
      infoText.setText("");
    });
    playBack.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuPlay, menuMain);
      infoText.setText("");
    });

    connectBack.setOnMouseClicked(event -> {
      fadeFromMenuToMenu(menuConnect, menuPlay);
      infoText.setText("");
    });

    initMenu(menuMain);
    initMenu(menuOptions);
    initMenu(menuSkins);
    initMenu(menuPlay);

    startPane.getChildren().add(menuMain);
    menuWithInfo.getChildren().addAll(startPane, infoText);
    getChildren().add(menuWithInfo);

    buttons = new HashMap<String, MenuObject>() {{
      put("playSolo", playSolo);
      put("playDuo", playDuo);
      put("playTrio", playTrio);
      put("playOnline", playOnline);
      put("connectCreateDuo", connectCreateDuo);
      put("connectCreateTrio", connectCreateTrio);
      put("connectPlay", connectPlay);
    }};
  }

  @Override
  public void reload() {
    getChildren().clear();
    getChildren().add(menuWithInfo);
  }

  @Override
  public Map<String, MenuObject> getButtonsMap() {
    return buttons;
  }

  private void fadeFromMenuToMenu(MenuBox from, MenuBox to) {
    FadeTransition frFrom = new FadeTransition(Duration.millis(200), from);
    frFrom.setFromValue(1);
    frFrom.setToValue(0);

    FadeTransition ftTo = new FadeTransition(Duration.millis(200), to);
    ftTo.setFromValue(0);
    ftTo.setToValue(1);

    frFrom.play();
    frFrom.setOnFinished(event -> {
      startPane.getChildren().remove(from);
      to.setOpacity(0);
      startPane.getChildren().add(to);
      ftTo.play();
    });
  }

  private void initMenu(MenuBox menu) {
    menu.setAlignment(Pos.BOTTOM_CENTER);
    menu.setMaxWidth(300);
    menu.setTranslateY(-20);
  }

  public String getConnectIP() {
    return connectEdit.getTextField().getText();
  }
}
