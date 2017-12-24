package app.menus.mainMenu;

import app.menus.menu.MenuObject;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainMenuButton extends MenuObject {
    private Text text;

    public MainMenuButton(String name){
        LinearGradient gradient = new LinearGradient(
                0, 0,
                1, 0,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.4, Color.GREY),
                new Stop(0.6, Color.GREY),
                new Stop(1, Color.TRANSPARENT)
        );

        Rectangle bg = new Rectangle(200, 30);
        bg.setOpacity(0.4);

        text = new Text(name);
        text.setFill(Color.DARKGREY);
        text.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, 22));

        setAlignment(Pos.CENTER);
        getChildren().addAll(bg, text);

        setOnMouseEntered(event -> text.setFill(Color.WHITE));
        setOnMouseExited(event -> {
            bg.setFill(Color.TRANSPARENT);
            text.setFill(Color.DARKGREY);
        });
        setOnMousePressed(event -> bg.setFill(gradient));
        setOnMouseReleased(event -> bg.setFill(Color.TRANSPARENT));
    }

    void setText(String new_text) {
        text.setText(new_text);
    }
}
