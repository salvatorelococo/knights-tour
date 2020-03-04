import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaveScreen {
    static Label label;
    static CustomButton[] saveButtons = new CustomButton[KnightsTour.MAX_SAVES];
    static Button cancelBtn;
    static byte answer;

    public static int display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Save game");

        label = new Label("Select slot where to save the game");

        for (byte i = 0; i < KnightsTour.MAX_SAVES; i++) {
            saveButtons[i] = new CustomButton("Save in slot " + (i + 1));
        }

        cancelBtn = new Button("Go Back");
        cancelBtn.setMinWidth(100);
        cancelBtn.setDefaultButton(true);

        // Layout
        VBox loadLayout = new VBox(20);
        loadLayout.setAlignment(Pos.CENTER);
        loadLayout.getChildren().add(label);
        for (Button btn : saveButtons) {
            loadLayout.getChildren().add(btn);
        }
        loadLayout.getChildren().add(cancelBtn);
        loadLayout.setPadding(KnightsTour.DEFAULT_PADDING);

        Scene scene = new Scene(loadLayout);

        // Actions
        window.setOnCloseRequest(e -> {
            e.consume();
            answer = -1;
            window.close();
        });

        for (byte i = 0; i < KnightsTour.MAX_SAVES; i++) {
            final byte j = i;
            saveButtons[i].setOnAction(e -> {
                // Default save name
                File save = new File(KnightsTour.SAVE_FOLDER, KnightsTour.SAVE_BASENAME + (j + 1) + KnightsTour.SAVE_EXT);
                if (save.exists()) {
                    if (Dialog.display("Overwrite")) {
                        answer = (byte) (j + 1);
                        window.close();
                    }
                } else {
                    answer = (byte) (j + 1);
                    window.close();
                }

            });
        }

        cancelBtn.setOnAction(e -> {
            answer = -1;
            window.close();
        });

        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

        return answer;
    }
}
