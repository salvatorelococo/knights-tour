import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class KnightsTour extends Application {
    // Final Variables
    static final byte ITERATIONS = 3;
    static final byte MAX_SAVES = 3;
    static final byte MAX_CHESSBOARD_SIZE = 20;
    static final byte MIN_CHESSBOARD_SIZE = 6;
    static final short PREF_BTN_WIDTH = 300;
    static final short PREF_BTN_HEIGHT = 50;
    static final String SAVE_BASENAME = "ktSave";
    static final String SAVE_FOLDER = "saves";
    static final String SAVE_EXT = ".sav";
    static final String TITLE = "Knight's Tour";

    // Other Final Variables
    static final Insets DEFAULT_PADDING = new Insets(25, 100, 25, 100);
    static final String DEFAULT_FONT = "Segoe UI";
    static final Font PARAGRAPH_FONT = new Font(DEFAULT_FONT, 12);
    static final Font BUTTON_FONT = new Font(DEFAULT_FONT, 16);
    static final Font TITLE_FONT = new Font(DEFAULT_FONT, 20);

    // Screen info
    static final Screen screen = Screen.getPrimary();
    static final Rectangle2D bounds = screen.getVisualBounds();
    static final short width = (short) bounds.getWidth();
    static final short height = (short) bounds.getHeight();

    // Settings Variables (default values)
    static byte autoplaySpeed = 1;
    static byte rows = 8;
    static byte columns = 8;
    static boolean helpBtn = true;
    static short squareMinSize = (short) ((height - 270) / rows);

    // CustomClass & JavaFX Variables
    Chessboard chessboard = new Chessboard();
    VBox mainLayout, htpLayout;
    GridPane gameLayout;
    Scene mainScene;
    Timeline timer;

    // Main
    Label mainTitleLbl;
    CustomButton mainNewGameBtn, mainContinueBtn, mainLoadGameBtn, mainSettingsBtn, mainHTPBtn, mainExitBtn;

    // How To Play
    Button htpGoBackBtn;
    Label[] htpTitle = new Label[4], htpText = new Label[4];
    Label htpTitleLbl, htpTextLbl;

    // Game Screen
    Button gameGoBackBtn, gameUndoBtn, gameSaveBtn, gameRestartBtn, gameAutoPlayBtn;
    Label gameSquaresCounterLbl, gameHintLbl;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Load configuration
        loadConfigVariables();

        // Main - Elements
        mainTitleLbl = new Label(TITLE);
        mainTitleLbl.setFont(new Font(DEFAULT_FONT, 96));
        mainTitleLbl.setPadding(new Insets(0, 0, 10, 0));

        mainNewGameBtn = new CustomButton("New Game");
        mainNewGameBtn.setDefaultButton(true);
        mainNewGameBtn.setOnAction(event -> {
            startNewGame();
            primaryStage.setMaximized(true);
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.setResizable(false);
        });

        mainContinueBtn = new CustomButton("Continue");
        if (!chessboard.load(MAX_SAVES + 1)) {
            mainContinueBtn.setDisable(true);
        }
        mainContinueBtn.setOnAction(event -> {
            setDisableButtons(false, gameRestartBtn, gameAutoPlayBtn);
            setGameLayout();
            gameUndoBtn.setDisable(true);
            primaryStage.setMaximized(true);
            primaryStage.setMinWidth(primaryStage.getWidth());
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.setResizable(false);
        });

        mainLoadGameBtn = new CustomButton("Load Game");
        mainLoadGameBtn.setOnAction(event -> {
            try {
                byte index = LoadScreen.display();

                if (index != -1) {
                    chessboard.load(index);
                    setDisableButtons(false, gameRestartBtn, gameAutoPlayBtn);
                    setGameLayout(chessboard.isHelpBtn());
                    gameUndoBtn.setDisable(true);
                    primaryStage.setMaximized(true);
                    primaryStage.setMinWidth(primaryStage.getWidth());
                    primaryStage.setMinHeight(primaryStage.getHeight());
                    primaryStage.setResizable(false);
                }
            } catch (Exception ignored) {}
        });

        mainSettingsBtn = new CustomButton("Settings");
        mainSettingsBtn.setOnAction(event -> {
            Settings.display(rows, columns, helpBtn);
            loadConfigVariables();
        });

        mainHTPBtn = new CustomButton("How To Play");
        mainHTPBtn.setOnAction(event -> mainScene.setRoot(getHtpLayout()));

        mainExitBtn = new CustomButton("Exit");
        mainExitBtn.setOnAction(event -> exit());

        // Main - Layout
        mainLayout = new VBox(20);
        mainLayout.getChildren().add(mainTitleLbl);
        mainLayout.getChildren().addAll(mainNewGameBtn, mainContinueBtn, mainLoadGameBtn, mainSettingsBtn, mainHTPBtn, mainExitBtn);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(50));

        // Game Screen - Elements
        gameGoBackBtn = new Button("Go Back");
        gameGoBackBtn.setMinWidth(150);
        gameGoBackBtn.setOnAction(e -> mainScene.setRoot(mainLayout));

        gameUndoBtn = new Button("Undo");
        gameUndoBtn.setMinWidth(150);
        gameUndoBtn.setOnAction(e -> {
            chessboard.undo();
            updateGameSquaresCounterLbl();
            gameUndoBtn.setDisable(true);
        });

        gameSaveBtn = new Button("Save");
        gameSaveBtn.setMinWidth(150);
        gameSaveBtn.setOnAction( e -> {
            int saveIndex = SaveScreen.display(); // NB: the returned value is already increased by 1
            if (saveIndex != -1) {
                chessboard.save(saveIndex);
            }
        });

        gameRestartBtn = new Button("Restart");
        gameRestartBtn.setMinWidth(150);
        gameRestartBtn.setOnAction( e -> {
            if (Dialog.display("Restart")) {
                startNewGame((byte) (chessboard.getSquares().length), (byte) (chessboard.getSquares()[0].length));
            }
        });

        timer = new Timeline(new KeyFrame(new Duration(autoplaySpeed * 100.0), e -> {
            try {
                chessboard.bestMove();
                updateGameSquaresCounterLbl();
                if (chessboard.isFull() || !chessboard.getAvailableMoves()) {
                    timer.stop();
                }
                updateButtons();
            } catch(Exception exc) {
                timer.stop();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);

        gameAutoPlayBtn = new Button("\u23F5 Auto-Play");
        gameAutoPlayBtn.setMinWidth(150);
        gameAutoPlayBtn.setOnAction(e -> {
            if ((timer.getStatus() == Timeline.Status.RUNNING)) {
                timer.stop();
                setDisableButtons(false, gameUndoBtn, gameSaveBtn, gameRestartBtn);
                gameAutoPlayBtn.setText("\u23F5 Auto-Play");
            } else {
                timer.play();
                setDisableButtons(true, gameUndoBtn, gameSaveBtn, gameRestartBtn);

                gameAutoPlayBtn.setText("\u23F8 Auto-Play");
            }
        });

        // Game - Labels
        gameSquaresCounterLbl = new Label();
        gameSquaresCounterLbl.setPrefWidth(300);
        gameSquaresCounterLbl.setFont(new Font(DEFAULT_FONT, 24));
        gameSquaresCounterLbl.setPadding(new Insets(0, 0, 0, 25));

        gameHintLbl = new Label();
        gameHintLbl.setFont(new Font(DEFAULT_FONT, 18));



        // How To Play - Elements
        int counterLbl = 0;

        htpTitleLbl = new Label("What's a knight's tour?");
        htpTextLbl = new Label("A knight's tour is a sequence of moves of a knight on a chessboard such that the knight visits every square only once." +
                " If the knight ends on a square that is one knight's move from the beginning square" +
                " (so that it could tour the board again immediately, following the same path), the tour is closed;" +
                " otherwise, it is open.");
        htpTitle[counterLbl] = htpTitleLbl;
        htpText[counterLbl++] = htpTextLbl;

        htpTitleLbl = new Label("How To Play");
        htpTextLbl = new Label("When starting a new game you will be able to select the starting square." +
                " After that you will be able to do one of the valid moves by clicking one of the enabled squares." +
                " You win if you cover the entire chessboard.");
        htpTitle[counterLbl] = htpTitleLbl;
        htpText[counterLbl++] = htpTextLbl;

        htpTitleLbl = new Label("Load, Save & Undo");
        htpTextLbl = new Label("You can save a game and load it in the future: there are " + MAX_SAVES + " available save slots." +
                " If a match is in progress when you close the game, you will be able to continue it next time you run the game by clicking on \"Continue\" button." +
                " During a match you can undo your moves, but only one at a time, by clicking on \"Undo\" button.");
        htpTitle[counterLbl] = htpTitleLbl;
        htpText[counterLbl++] = htpTextLbl;

        htpTitleLbl = new Label("Settings");
        htpTextLbl = new Label("You can edit the size of the chessboard and enable/disable the \"Auto-Play\" button.");
        htpTitle[counterLbl] = htpTitleLbl;
        htpText[counterLbl] = htpTextLbl;

        for (int i = 0; i < 4; i++) {
            htpTitle[i].setFont(TITLE_FONT);
            htpText[i].setPrefWidth(1920);
            htpText[i].setWrapText(true);
        }

        htpGoBackBtn = new Button("Go Back");
        htpGoBackBtn.setMinWidth(150);
        htpGoBackBtn.setOnAction(e -> mainScene.setRoot(getMainLayout()));

        // How To Play - Layout
        VBox[] htpTextLayout = new VBox[4];

        for (int i = 0; i < 4; i++) {
            htpTextLayout[i] = new VBox(5);
            htpTextLayout[i].getChildren().addAll(htpTitle[i], htpText[i]);
        }

        htpLayout = new VBox(20);
        for (int i = 0; i < 4; i++) {
            htpLayout.getChildren().add(htpTextLayout[i]);
        }
        htpLayout.getChildren().add(htpGoBackBtn);
        htpLayout.setPadding(DEFAULT_PADDING);
        htpLayout.setAlignment(Pos.CENTER);

        // Stage initializing
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            exit();
        });

        mainScene = new Scene(mainLayout);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());

    }

    // Methods
    private void exit() {
        if (Dialog.display("Exit")) {
            try {
                chessboard.save(MAX_SAVES + 1);
            } catch (Exception ignored) {}
            Platform.exit();
        }
    }

    private VBox getHtpLayout() {
        return htpLayout;
    }

    private VBox getMainLayout() {
        return mainLayout;
    }

    private void loadConfigVariables() {
        try {
            File configPath = new File("kt.config");
            if (configPath.exists()) {
                FileInputStream configStream = new FileInputStream(configPath);
                ObjectInputStream config = new ObjectInputStream(configStream);

                rows = config.readByte();
                columns = config.readByte();
                helpBtn = config.readBoolean();
                squareMinSize = (short) (Math.min(((width - 650) / columns), ((height - 270) / rows)));

                config.close();
            }
        } catch(Exception ignored) {}
    }

    private void updateGameSquaresCounterLbl() {
        gameSquaresCounterLbl.setText("Occupied Squares: " + ((chessboard.getOccupiedSquares() < 10) ? " " : "") + chessboard.getOccupiedSquares());
    }

    private void generateGameLayout(boolean helpBtn) {
        GridPane chessLayout = new GridPane();

        for (byte i = 0; i < chessboard.getSquares().length; i++) {
            for (byte j = 0; j < chessboard.getSquares()[i].length; j++) {
                final byte r = i;
                final byte c = j;
                chessboard.getSquares()[i][j].getButton().setOnAction(e -> {
                    chessboard.setPosition(r, c);
                    updateGameSquaresCounterLbl();
                    updateButtons();
                });

                chessLayout.add(chessboard.getSquares()[i][j].getButton(), j, i);
            }
        }

        chessLayout.add(gameSquaresCounterLbl, chessboard.getSquares()[0].length + 1, 0);
        chessLayout.setPadding(DEFAULT_PADDING);

        HBox gameActionsLayout = new HBox(10);
        gameActionsLayout.getChildren().add(gameHintLbl);
        gameActionsLayout.setPadding(DEFAULT_PADDING);
        gameActionsLayout.setAlignment(Pos.CENTER);

        HBox gameButtons1 = new HBox(10);
        gameButtons1.getChildren().add(gameUndoBtn);
        if (helpBtn) {
            gameButtons1.getChildren().add(gameAutoPlayBtn);
        }
        gameButtons1.setAlignment(Pos.CENTER);

        HBox gameButtons2 = new HBox(10);
        gameButtons2.getChildren().addAll(gameGoBackBtn, gameRestartBtn, gameSaveBtn);
        gameButtons2.setAlignment(Pos.CENTER);

        updateGameSquaresCounterLbl();
        gameLayout = new GridPane();
        gameLayout.setVgap(10);
        gameLayout.setHgap(25);
        gameLayout.addColumn(0, chessLayout, gameActionsLayout, gameButtons1, gameButtons2);
        gameLayout.setPadding(DEFAULT_PADDING);
        gameLayout.setAlignment(Pos.CENTER);
    }

    private void setGameLayout(boolean helpBtn) {
        generateGameLayout(helpBtn);
        mainScene.setRoot(gameLayout);
    }

    private void setGameLayout() {
        setGameLayout(helpBtn);
    }

    private void updateButtons() {
        if (chessboard.isFull()) {
            gameHintLbl.setText("Congratulations! You won!");
            setDisableButtons(true, mainContinueBtn, gameSaveBtn, gameUndoBtn, gameAutoPlayBtn);
            gameAutoPlayBtn.setText("\u23f5 Auto-Play");
            if (Dialog.display("EndGame", gameHintLbl.getText())) {
                startNewGame();
            }
        } else if (!chessboard.getAvailableMoves()) {
            gameHintLbl.setText("No more available moves. Try again!");
            setDisableButtons(true, gameSaveBtn, gameUndoBtn, gameAutoPlayBtn, mainContinueBtn);
            gameAutoPlayBtn.setText("\u23f5 Auto-Play");
            if (Dialog.display("EndGame", gameHintLbl.getText())) {
                startNewGame();
            }
        } else if (chessboard.getOccupiedSquares() == 1) {
            setDisableButtons(false, gameRestartBtn, gameAutoPlayBtn, gameSaveBtn, mainContinueBtn);
            gameHintLbl.setText("Do your next move!");
        } else if (chessboard.getOccupiedSquares() > 1 && timer.getStatus() != Timeline.Status.RUNNING) {
            gameUndoBtn.setDisable(false);
        }
    }

    private void setDisableButtons(boolean value, Button... buttons) {
        for (Button btn : buttons) {
            btn.setDisable(value);
        }
    }

    private void startNewGame(byte rows, byte columns) {
        chessboard.init(rows, columns, (short) (Math.min(((width - 650) / columns), ((height - 270) / rows))));
        setGameLayout();
        setDisableButtons(true, gameRestartBtn, gameSaveBtn, gameUndoBtn, mainContinueBtn, gameAutoPlayBtn);
        gameHintLbl.setText("Select the starting square.");
        updateGameSquaresCounterLbl();
    }

    private void startNewGame() {
        startNewGame(rows, columns);
    }
}
