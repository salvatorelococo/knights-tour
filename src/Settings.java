import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Settings {

    static Button settingsSaveBtn, settingsCancelBtn;
    static Label settingsAutoPlayBtnLbl, settingsRowsNumberLbl, settingsColumnsNumberLbl;
    static CheckBox settingsHelpBtn;
    static Spinner<Integer> settingsRowsNumber, settingsColumnsNumber;

    public static void display(byte rows, byte columns, boolean helpBtn) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");

        settingsRowsNumberLbl = new Label("Select number of rows");
        settingsColumnsNumberLbl = new Label("Select number of columns");
        settingsAutoPlayBtnLbl = new Label("Enable Auto-Play button");

        settingsRowsNumber = new Spinner<>(KnightsTour.MIN_CHESSBOARD_SIZE, KnightsTour.MAX_CHESSBOARD_SIZE, rows);
        settingsRowsNumber.setMaxWidth(70);

        settingsColumnsNumber = new Spinner<>(KnightsTour.MIN_CHESSBOARD_SIZE, KnightsTour.MAX_CHESSBOARD_SIZE, columns);
        settingsColumnsNumber.setMaxWidth(70);

        settingsHelpBtn = new CheckBox();
        settingsHelpBtn.setSelected(helpBtn);

        settingsSaveBtn = new Button("Save");
        settingsSaveBtn.setMinWidth(100);
        settingsSaveBtn.setDefaultButton(true);
        settingsSaveBtn.setOnAction(event -> {
            saveConfigVariables(settingsRowsNumber.getValue(), settingsColumnsNumber.getValue(), settingsHelpBtn.isSelected());
            window.close();
        });

        settingsCancelBtn = new Button("Cancel");
        settingsCancelBtn.setMinWidth(100);
        settingsCancelBtn.setOnAction(event -> window.close());

        // Settings - Layout
        GridPane settingsLayout = new GridPane();
        settingsLayout.setVgap(10);
        settingsLayout.setHgap(25);
        settingsLayout.addColumn(0, settingsRowsNumberLbl, settingsColumnsNumberLbl, settingsAutoPlayBtnLbl);
        settingsLayout.addColumn(1, settingsRowsNumber, settingsColumnsNumber, settingsHelpBtn);
        settingsLayout.setPadding(KnightsTour.DEFAULT_PADDING);
        settingsLayout.addRow(3, settingsSaveBtn, settingsCancelBtn);
        Scene settingsScene = new Scene(settingsLayout);

        window.setScene(settingsScene);
        window.setResizable(false);
        window.showAndWait();
    }

    private static void saveConfigVariables(int rows, int columns, boolean helpBtn) {
        try {
            File configPath = new File("kt.config");
            FileOutputStream configStream = new FileOutputStream(configPath);
            ObjectOutputStream config = new ObjectOutputStream(configStream);

            config.writeByte((byte) (rows));
            config.writeByte((byte) (columns));
            config.writeBoolean(helpBtn);

            config.close();
        } catch(Exception ignored) {}
    }
}
