package hr.algebra.mastermind.model;

import hr.algebra.mastermind.enums.Role;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class GameState implements Serializable {
    private final String selectedColor;
    private final String selectedHintColor;

    private final List<String> codeColors;
    private final List<List<String>> colorsOfGuessCircles;
    private final List<List<String>> colorsOfHintCircles;

    private final int indexOfCurrentRow;

    private final int numberOfRounds;
    private final Player player1;
    private final Player player2;
    private final Role currentTurn;

    private final boolean isStartGameVisible;
    private final boolean isBtnSetCodeVisible;
    private final boolean isBtnNextTurnVisible;

    public GameState(Paint selectedColor, Paint selectedHintColor, List<Circle> codeCircles, List<CodeGuessRow> codeGuessRows, int indexOfCurrentRow, int numberOfRounds, Player player1, Player player2, Role currentTurn, boolean isStartGameVisible , boolean isBtnSetCodeVisible, boolean isBtnNextTurnVisible) {
        this.selectedColor = selectedColor.toString();
        this.selectedHintColor = selectedHintColor.toString();
        this.indexOfCurrentRow = indexOfCurrentRow;
        this.numberOfRounds = numberOfRounds;
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = currentTurn;
        this.isStartGameVisible = isStartGameVisible;
        this.isBtnSetCodeVisible = isBtnSetCodeVisible;
        this.isBtnNextTurnVisible = isBtnNextTurnVisible;

        codeColors = new ArrayList<>();
        colorsOfGuessCircles = new ArrayList<>();
        colorsOfHintCircles = new ArrayList<>();

        for(var codeCircle : codeCircles){
            codeColors.add(codeCircle.getFill().toString());
        }

        for(var codeGuessRow : codeGuessRows){
            colorsOfGuessCircles.add(codeGuessRow.getGuessColors());
            colorsOfHintCircles.add(codeGuessRow.getHintColors());
        }
    }

    public Paint getSelectedColor() {
        return Color.web(selectedColor);
    }

    public Paint getSelectedHintColor() {
        return Color.web(selectedHintColor);
    }

    public List<String> getCodeColors() {
        return codeColors;
    }

    public List<List<String>> getColorsOfGuessCircles() {
        return colorsOfGuessCircles;
    }

    public List<List<String>> getColorsOfHintCircles() {
        return colorsOfHintCircles;
    }

    public int getIndexOfCurrentRow() {
        return indexOfCurrentRow;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Role getCurrentTurn() {
        return currentTurn;
    }

    public boolean getIsStartGameVisible(){
        return isStartGameVisible;
    }

    public boolean getIsBtnSetCodeVisible(){
        return isBtnSetCodeVisible;
    }

    public boolean getIsBtnNextTurnVisible(){
        return isBtnNextTurnVisible;
    }
}