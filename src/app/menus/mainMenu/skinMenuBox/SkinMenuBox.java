package app.menus.mainMenu.skinMenuBox;

import static javax.swing.UIManager.put;

import app.SkinSettings;
import app.menus.mainMenu.MainMenuButton;
import app.menus.menu.MenuBox;
import app.menus.menu.MenuObject;
import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Map;

public class SkinMenuBox extends MenuBox {

    private int player1Skin;
    private int player2Skin;
    private int player3Skin;
    private int skinVariations;
    private Map<String, MenuObject> buttons;

    public SkinMenuBox(SkinSettings skinSettings) {
        player1Skin = skinSettings.getPlayer1Skin();
        player2Skin = skinSettings.getPlayer2Skin();
        player3Skin = skinSettings.getPlayer3Skin();
        skinVariations = skinSettings.getSkinVariations();

        VBox root = new VBox(10);
        HBox previewBox = new HBox(10);
        HBox buttonsBox = new HBox();
        root.setAlignment(Pos.CENTER);
        previewBox.setAlignment(Pos.CENTER);
        buttonsBox.setAlignment(Pos.CENTER);

        SkinMenuBoxPreviewBox previewBox1 = new SkinMenuBoxPreviewBox(
                getImage(skinSettings.getPlayer1Skin(), 1)
        );
        SkinMenuBoxPreviewBox previewBox2 = new SkinMenuBoxPreviewBox(
                getImage(skinSettings.getPlayer2Skin(), 2)
        );
        SkinMenuBoxPreviewBox previewBox3 = new SkinMenuBoxPreviewBox(
                getImage(skinSettings.getPlayer3Skin(), 3)
        );
        MenuObject skinAccept = new MainMenuButton("ACCEPT");
        MenuObject skinDecline = new MainMenuButton("DECLINE");

        Map<String, MenuObject> previewButtons1 = previewBox1.getButtonsMap();
        Map<String, MenuObject> previewButtons2 = previewBox2.getButtonsMap();
        Map<String, MenuObject> previewButtons3 = previewBox3.getButtonsMap();

        previewButtons1.get("buttonPrev")
                .setOnMouseClicked(event -> {
                    player1Skin = Math.floorMod((player1Skin - 1), skinVariations);
                    previewBox1.setImage(getImage(player1Skin, 1));
                });
        previewButtons1.get("buttonNext")
                .setOnMouseClicked(event -> {
                    player1Skin = Math.floorMod((player1Skin + 1), skinVariations);
                    previewBox1.setImage(getImage(player1Skin, 1));
                });
        previewButtons2.get("buttonPrev")
                .setOnMouseClicked(event -> {
                    player2Skin = Math.floorMod((player2Skin - 1), skinVariations);
                    previewBox2.setImage(getImage(player2Skin, 2));
                });
        previewButtons2.get("buttonNext")
                .setOnMouseClicked(event -> {
                    player2Skin = Math.floorMod((player2Skin + 1), skinVariations);
                    previewBox2.setImage(getImage(player2Skin, 2));
                });
        previewButtons3.get("buttonPrev")
                .setOnMouseClicked(event -> {
                    player3Skin = Math.floorMod((player3Skin - 1), skinVariations);
                    previewBox3.setImage(getImage(player3Skin, 3));
                });
        previewButtons3.get("buttonNext")
                .setOnMouseClicked(event -> {
                    player3Skin = Math.floorMod((player3Skin + 1), skinVariations);
                    previewBox3.setImage(getImage(player3Skin, 3));
                });

        skinAccept.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
            skinSettings.setSkins(player1Skin, player2Skin, player3Skin));
        skinDecline.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            player1Skin = skinSettings.getPlayer1Skin();
            player2Skin = skinSettings.getPlayer2Skin();
            player3Skin = skinSettings.getPlayer3Skin();
            previewBox1.setImage(getImage(player1Skin, 1));
            previewBox2.setImage(getImage(player2Skin, 2));
            previewBox3.setImage(getImage(player3Skin, 3));
        });

        buttons = new HashMap<String, MenuObject>() {{
            put("skinAccept", skinAccept);
            put("skinDecline", skinDecline);
        }};


        previewBox.getChildren().addAll(previewBox1, previewBox2, previewBox3);
        buttonsBox.getChildren().addAll(skinAccept, skinDecline);
        root.getChildren().addAll(previewBox, buttonsBox);
        getChildren().add(root);
    }

    public Map<String, MenuObject> getButtonsMap() {
        return buttons;
    }

    private Image getImage(int skinID, int player) {
        return new Image(
                String.format("images/Skin%d/Head%d.png", skinID, player),
                100,
                0,
                true,
                true
        );
    }
}
