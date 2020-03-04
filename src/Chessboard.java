import java.io.*;

public class Chessboard {
    // Variables
    private Square[][] squares;
    private byte oldRow, oldCol; // last position for undo
    private byte currentRow, currentCol; // current position
    private byte[] vertical = {-1, -2, -2, -1, 1, 2, 2, 1}; // change row
    private byte[] horizontal = {2, 1, -1, -2, -2, -1, 1, 2}; // change column
    private boolean helpBtn;

    // methods
    private boolean getAutoPlayBtn() {
        return KnightsTour.helpBtn;
    }

    public void init(byte rows, byte columns, short size) {
        helpBtn = getAutoPlayBtn();
        setSquares(new Square[rows][columns]);
        for (byte i = 0; i < rows; i++) {
            for (byte j = 0; j < columns; j++) {
                getSquares()[i][j] = new Square(size);
            }
        }
    }

    public void init(byte rows, byte columns) {
        init(rows, columns, KnightsTour.squareMinSize);
    }

    public byte bestMove(byte row, byte col, byte iter) {
        byte min = 127;
        byte move = -1;

        byte selectedRow = currentRow;
        byte selectedCol = currentCol;

        for (byte i = 0; i < vertical.length; i++) {
            if (isValidMove(i)) {
                byte nextRow = (byte) (currentRow + vertical[i]);
                byte nextCol = (byte) (currentCol + horizontal[i]);

                if (getSquares()[nextRow][nextCol].getAccessibility() < min) {
                    min = getSquares()[nextRow][nextCol].getAccessibility();
                    move = i;

                    selectedRow = nextRow;
                    selectedCol = nextCol;
                } else if (getSquares()[nextRow][nextCol].getAccessibility() == min && iter != 0) {
                    if(bestMove(nextRow, nextCol, (byte) (iter - 1)) < bestMove(selectedRow, selectedCol , (byte) (iter - 1))) {
                        move = i;

                        selectedRow = nextRow;
                        selectedCol = nextCol;
                    }
                }
            }
        }

        if (iter == KnightsTour.ITERATIONS) {
            return move;
        } else {
            return min;
        }
    }

    public void bestMove() {
        byte move = bestMove(currentRow, currentCol, KnightsTour.ITERATIONS);
        setPosition((byte) (currentRow + vertical[move]), (byte) (currentCol + horizontal[move]));
    }

    private void disableAll() {
        for (Square[] row : squares) {
            for (Square square : row) {
                square.getButton().setDisable(true);
                if(!square.isVisited()) {
                    square.getButton().setStyle(Square.disabledCSS);
                }
            }
        }
    }

    public void getAccessibility() {
        for (byte i = 0; i < getSquares().length; i++) {
            for (byte j = 0; j < getSquares()[0].length; j++) {
                if (!(getSquares()[i][j].isVisited())) {
                    byte counter = 0;
                    for (byte k = 0; k < vertical.length; k++) {
                        try {
                            if (!(getSquares()[i + vertical[k]][j + horizontal[k]].isVisited())) {
                                counter++;
                            }
                        } catch (Exception ignored) {}
                    }
                    getSquares()[i][j].setAccessibility(counter);
                }
            }
        }
    }

    public boolean isValidMove(byte move) {
        try {
            return (!getSquares()[currentRow + vertical[move]][currentCol + horizontal[move]].isVisited());
        } catch (Exception exc) {
            return false;
        }
    }

    public boolean getAvailableMoves() {
        boolean flag = false;
        disableAll();

        for (byte i = 0; i < vertical.length; i++) {
            try {
                if (isValidMove(i)) {
                    getSquares()[currentRow + vertical[i]][currentCol + horizontal[i]].getButton().setDisable(false);
                    getSquares()[currentRow + vertical[i]][currentCol + horizontal[i]].getButton().setStyle(Square.defaultCSS);
                    flag = true;
                }
            } catch (Exception ignored) {}
        }

        return flag;
    }

    public short getOccupiedSquares() {
        short counter = 0;
        for (Square[] row : getSquares()) {
            for (Square square : row) {
                if (square.isVisited()) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public boolean isFull() {
        for (Square[] row : getSquares()) {
            for (Square square : row) {
                if (!square.isVisited()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setPosition(byte row, byte col) {

        oldRow = currentRow;
        oldCol = currentCol;
        if (oldRow < squares.length && oldCol < squares[0].length) {
            getSquares()[oldRow][oldCol].setCurrent(false);
        }

        currentRow = row;
        currentCol = col;
        getSquares()[currentRow][currentCol].setCurrent(true);

        getAccessibility();
        getAvailableMoves();
    }

    public void undo() {
        getSquares()[currentRow][currentCol].setVisited(false);

        currentRow = oldRow;
        currentCol = oldCol;

        getSquares()[currentRow][currentCol].setCurrent(true);

        getAccessibility();
        getAvailableMoves();
    }

//    public void delete(int index) {
//        File savePath = new File(KnightsTour.SAVE_FOLDER, KnightsTour.SAVE_BASENAME + index + KnightsTour.SAVE_EXT);
//
//        if (savePath.exists()) {
//            savePath.delete();
//        }
//    }

    public boolean load(int index) {
        try {
            File savePath = new File(KnightsTour.SAVE_FOLDER, KnightsTour.SAVE_BASENAME + index + KnightsTour.SAVE_EXT);

            if (savePath.exists()) {
                FileInputStream loadFile = new FileInputStream(savePath);
                ObjectInputStream load = new ObjectInputStream(loadFile);

                byte rows = load.readByte();
                byte columns = load.readByte();
                short size = load.readShort();

                init(rows, columns, size);
                helpBtn = load.readBoolean();

                for (Square[] row : squares) {
                    for (Square square : row) {
                        if (load.readBoolean()) {
                            square.setCurrent(true);
                            square.setCurrent(false);
                        }
                    }
                }
                currentRow = load.readByte();
                currentCol = load.readByte();

                setPosition(currentRow, currentCol);
                load.close();
                return true;
            }
            return false;
        } catch (Exception exc) {
            return false;
        }
    }

    public boolean save(int index) {
        try {
            File folder = new File(KnightsTour.SAVE_FOLDER);
            if (!folder.exists()) {
                if(!folder.mkdir()) {
                    throw new Exception("Unable to create the save folder!");
                }
            }

            File savePath = new File(KnightsTour.SAVE_FOLDER, ((index == KnightsTour.MAX_SAVES + 1) ? "ktAutoSave" : (KnightsTour.SAVE_BASENAME + index)) + KnightsTour.SAVE_EXT);
            FileOutputStream saveFile = new FileOutputStream(savePath);
            ObjectOutputStream save = new ObjectOutputStream(saveFile);

            save.writeByte(squares.length);
            save.writeByte(squares[0].length);
            save.writeShort((short)(squares[0][0].getButton().getWidth()));
            save.writeBoolean(KnightsTour.helpBtn);

            for (Square[] row : squares) {
                for (Square square : row) {
                    save.writeBoolean(square.isVisited());
                }
            }
            save.writeByte(currentRow);
            save.writeByte(currentCol);

            save.close();
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public Square[][] getSquares() {
        return squares;
    }

    public void setSquares(Square[][] squares) {
        this.squares = squares;
    }

    public boolean isHelpBtn() {
        return helpBtn;
    }
}