import javafx.scene.control.Button;

public class CustomButton extends Button {
    public CustomButton(String text) {
        super(text);
        this.setMinWidth(KnightsTour.PREF_BTN_WIDTH);
        this.setPrefHeight(KnightsTour.PREF_BTN_HEIGHT);
        this.setFont(KnightsTour.BUTTON_FONT);
    }
}
