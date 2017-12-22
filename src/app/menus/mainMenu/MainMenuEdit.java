package app.menus.mainMenu;

import app.menus.menu.MenuObject;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MainMenuEdit extends MenuObject {
  private TextField textField;

  public MainMenuEdit(String name){
    textField = new TextField();
    textField.setMaxWidth(200);

    VBox root = new VBox();

    Text text = new Text(name);
    text.setFill(Color.DARKGREY);
    text.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, 22));
    text.setTextAlignment(TextAlignment.CENTER);

    root.setAlignment(Pos.CENTER);
    root.getChildren().addAll(text, textField);
    setAlignment(Pos.CENTER);
    getChildren().addAll(root);

    setOnMouseEntered(event -> text.setFill(Color.WHITE));
    setOnMouseExited(event -> text.setFill(Color.DARKGREY));
  }

  public TextField getTextField() {
    return textField;
  }
}
