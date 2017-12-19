package app.menus.mainMenu.skinMenuBox;

import java.util.HashMap;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import app.menus.menu.MenuObject;
import javafx.scene.layout.VBox;
import model.utils.Direction;
import java.util.Map;

public class SkinMenuBoxPreviewBox extends StackPane {

    private ImageView imageView;
    private Map<String, MenuObject> buttons;

    public SkinMenuBoxPreviewBox(Image image){
        setAlignment(Pos.CENTER);
        this.imageView = new ImageView(image);

        VBox box = new VBox();
        MenuObject buttonPrev = new SkinMenuBoxArrowButton(Direction.Up);
        MenuObject buttonNext = new SkinMenuBoxArrowButton(Direction.Down);
        buttons = new HashMap<String, MenuObject>(){{
            put("buttonPrev", buttonPrev);
            put("buttonNext", buttonNext);
        }};

        box.getChildren().addAll(buttonPrev, this.imageView, buttonNext);
        getChildren().add(box);
    }

    public void setImage(Image image) {
        this.imageView.setImage(image);
    }

    public Map<String, MenuObject> getButtonsMap() {
        return buttons;
    }
}
