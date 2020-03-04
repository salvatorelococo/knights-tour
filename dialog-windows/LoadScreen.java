import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadScreen {
    static Label label;
    static CustomButton[] loadButtons = new CustomButton[KnightsTour.MAX_SAVES];
    static Button cancelBtn;
    static byte answer;

    public static byte display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Load game");

        label = new Label("Select game to load");

        for (byte i = 0; i < KnightsTour.MAX_SAVES; i++) {
            loadButtons[i] = new CustomButton("Load save " + (i + 1));

            File save = new File(KnightsTour.SAVE_FOLDER, KnightsTour.SAVE_BASENAME + (i + 1) + KnightsTour.SAVE_EXT);
            if (!save.exists()) {
                loadButtons[i].setDisable(true);
            }
        }

        cancelBtn = new Button("Cancel");
        cancelBtn.setMinWidth(100);
        cancelBtn.setDefaultButton(true);

        // Layout
        VBox loadLayout = new VBox(20);
        loadLayout.setAlignment(Pos.CENTER);
        loadLayout.getChildren().add(label);
        for (Button btn : loadButtons) {
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
            loadButtons[i].setOnAction(e -> {
                answer = (byte) (j + 1);
                window.close();
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
