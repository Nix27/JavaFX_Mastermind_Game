package hr.algebra.mastermind.controller;

import hr.algebra.mastermind.enums.Role;
import hr.algebra.mastermind.model.CodeGuessRow;
import hr.algebra.mastermind.model.Player;
import hr.algebra.mastermind.utils.DialogUtils;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class MastermindController {
    private final int NUM_OF_GUESS_ROWS = 10;

    public FlowPane guessColorsFlowPane;
    public FlowPane hintColorsFlowPane;
    public HBox codeHBox;
    public VBox guessRowsVBox;
    public Button startBtn;
    public Button nextRowBtn;

    private final Paint defaultColor = Color.web("#848484");
    private Paint selectedColor;
    private Paint selectedHintColor;

    private final List<Circle> codeCircles = new ArrayList<>();
    private final List<CodeGuessRow> codeGuessRows = new ArrayList<>();

    private CodeGuessRow currentRow;

    private Player player1;
    private Player player2;

    public void initialize(){
        addEventToGuessColorCircles();
        addEventToHintColorCircles();
        initCodeCircles();
        initCodeGuessRows();
        addEventToGuessCircles();
        addEventToHintCircles();

        currentRow = codeGuessRows.get(0);
        player1 = new Player(Role.Codemaker);
        player2 = new Player(Role.Codebreaker);
    }

    private void initCodeCircles() {
        for(var node : codeHBox.getChildren()){
            if(node instanceof Circle codeCircle){
                codeCircle.setOnMouseClicked(e -> {
                    if(selectedColor != null){
                        codeCircle.setFill(selectedColor);
                    }
                });

                codeCircles.add(codeCircle);
            }
        }
    }

    private void addEventToHintCircles() {
        for(var row : codeGuessRows) {
            for(var hintCircle : row.getHintCircles()){
                hintCircle.setOnMouseClicked(e -> {
                    if(selectedHintColor != null)
                        hintCircle.setFill(selectedHintColor);
                });
            }
        }
    }

    private void addEventToGuessCircles() {
        for(var row : codeGuessRows) {
            for(var guessCircle : row.getGuessCircles()){
                guessCircle.setOnMouseClicked(e -> {
                    if(selectedColor != null)
                        guessCircle.setFill(selectedColor);
                });
            }
        }
    }

    private void initCodeGuessRows() {
        for(var node : guessRowsVBox.getChildren()){
            if(node instanceof HBox newRow){
                codeGuessRows.add(new CodeGuessRow(newRow));
            }
        }
    }

    private void addEventToGuessColorCircles(){
        for(var node : guessColorsFlowPane.getChildren()){
            if(node instanceof Circle colorCircle){
                colorCircle.setOnMouseClicked(e -> selectedColor = colorCircle.getFill());
            }
        }
    }

    private void addEventToHintColorCircles(){
        for(var node: hintColorsFlowPane.getChildren()){
            if(node instanceof Circle hintColorCircle){
                hintColorCircle.setOnMouseClicked(e -> selectedHintColor = hintColorCircle.getFill());
            }
        }
    }

    private boolean isValidCode(){
        boolean isValid = true;

        for(var codeCircle : codeCircles){
            if(codeCircle.getFill().equals(defaultColor)){
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    public void startGuessing(){
        if(!isValidCode()){
            DialogUtils.showInvalidCodeWarning();
        }

        currentRow.setActive(true);
    }

    public void nextRow(){
        currentRow.setActive(false);

        int currentRowIndex = codeGuessRows.indexOf(currentRow);
        currentRow = codeGuessRows.get(currentRowIndex + 1);

        currentRow.setActive(true);

        if(codeGuessRows.indexOf(currentRow) == (NUM_OF_GUESS_ROWS - 1)){
            nextRowBtn.setDisable(true);
        }
    }
}
