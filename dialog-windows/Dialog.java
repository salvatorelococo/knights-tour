import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dialog {

    static String title, message, confirm, cancel;
    static Label messageLbl;
    static Button cancelBtn, confirmBtn;
    static boolean answer;

    public static boolean display(String type, String preMessage) {
        switch(type) {
            case "Exit":
                title = "Confirm Exit";
                message = "Are you sure you want to exit Knight's Tour?";
                confirm = "Yes, Exit";
                break;
            case "EndGame":
                title = "End of the game";
                message = ((preMessage != null) ? preMessage + " " : "") + "Do you want to play again?";
                confirm = "Yes, Play Again";
                break;
            case "Overwrite":
                title = "Existing save";
                message = "The slot is already taken. Do you want to overwrite the old save?";
                confirm = "Yes, Overwrite";
                break;
            case "Restart":
                title = "Restart game";
                message = "Are you sure you want to restart?";
                confirm = "Yes, Restart";
                break;
            default:
                title = "Confirm Dialog";
                message = "Are you sure?";
                confirm = "Yes";
                break;
        }

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        messageLbl = new Label( message);
        messageLbl.setTextAlignment(TextAlignment.CENTER);

        confirmBtn = new Button(confirm);
        confirmBtn.setMinWidth(100);
        confirmBtn.setDefaultButton(true);

        cancelBtn = new Button("No, Cancel");
        cancelBtn.setMinWidth(100);

        // Layout
        HBox buttonsLayout = new HBox(20);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(confirmBtn, cancelBtn);

        VBox layout = new VBox(20);
        layout.setPadding(KnightsTour.DEFAULT_PADDING);
        layout.getChildren().addAll(messageLbl, buttonsLayout);
        layout.setAlignment(Pos.CENTER);


        Scene scene = new Scene(layout);

        // Actions
        confirmBtn.setOnAction(event -> {
            answer = true;
            window.close();
        });

        cancelBtn.setOnAction(event -> {
            answer = false;
            window.close();
        });

        window.setScene(scene);
        window.setResizable(false);
        window.showAndWait();

        return answer;
    }

    public static boolean display(String type) {
        return display(type, "");
    }
}
