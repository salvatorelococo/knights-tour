import javafx.scene.control.Button;

public class Square {
    // Variables
    static final String currentCSS = "-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: linear-gradient(green, yellow); -fx-opacity: 1.0;";
    static final String visitedCSS = "-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: linear-gradient(#aaa, #333); -fx-opacity: 0.6;";
    static final String disabledCSS = "-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: linear-gradient(#ff6a00, #e88741); -fx-opacity: 0.5;";
    static final String defaultCSS = "-fx-border-width: 2px; -fx-border-color: black; -fx-background-color: linear-gradient(#ff6a00, #e88741); -fx-opacity: 1.0;";

    private boolean current = false;
    private boolean visited = false;
    private byte accessibility = 0;
    private Button squareBtn = new Button();

    // Constructors
    public Square(short size) {
        squareBtn.setMinSize(size, size);
        squareBtn.setFocusTraversable(false);
        squareBtn.setStyle(defaultCSS);
    }

    // Methods
    // Getter
    public boolean isCurrent() {
        return current;
    }

    public boolean isVisited() {
        return visited;
    }

    public byte getAccessibility() {
        return accessibility;
    }

    public Button getButton() {
        return squareBtn;
    }

    // Setter
    public void setAccessibility(byte accessibility) {
        this.accessibility = accessibility;
        if (accessibility > 0) {
            setVisited(false);
        }
    }

    public void setButton(Button button) {
        this.squareBtn = button;
    }

    public void setCurrent(boolean current) {
        this.current = current;
        if (current) {
            setVisited(true);
            squareBtn.setStyle(currentCSS);
        } else if (visited) {
            squareBtn.setStyle(visitedCSS);
        }
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
        if (visited) {
            setAccessibility((byte) 0);
            squareBtn.setDisable(true);
        } else {
            this.current = false;
            squareBtn.setDisable(false);
        }
    }
}
