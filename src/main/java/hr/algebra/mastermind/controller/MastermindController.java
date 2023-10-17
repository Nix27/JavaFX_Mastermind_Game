package hr.algebra.mastermind.controller;

import hr.algebra.mastermind.enums.Role;
import hr.algebra.mastermind.model.CodeGuessRow;
import hr.algebra.mastermind.model.Player;
import hr.algebra.mastermind.utils.DialogUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
    public Button btnSetCode;
    public Button btnStartGame;
    public Button btnNextRow;
    public Label lbPlayer1Role;
    public Label lbPlayer1Points;
    public Label lbPlayer2Role;
    public Label lbPlayer2Points;
    public Spinner<Integer> spNumberOfRounds;

    private final Paint defaultCircleColor = Color.web("#848484");
    private Paint selectedColor;
    private Paint selectedHintColor;

    private final List<Circle> codeCircles = new ArrayList<>();
    private final List<CodeGuessRow> codeGuessRows = new ArrayList<>();

    private CodeGuessRow currentRow;

    private int numberOfRounds;
    private Player player1;
    private Player player2;

    private final SpinnerValueFactory<Integer> spinnerValueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 2, 2);

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

        showPlayerInfo();

        spNumberOfRounds.setValueFactory(spinnerValueFactory);
        btnSetCode.setVisible(false);
        btnNextRow.setVisible(false);
    }

    private void initCodeCircles() {
        for(var node : codeHBox.getChildren()){
            if(node instanceof Circle codeCircle){
                codeCircle.setOnMouseClicked(e -> {
                    if(selectedColor != null){
                        codeCircle.setFill(selectedColor);
                    }
                });

                codeCircle.setDisable(true);
                codeCircles.add(codeCircle);
            }
        }
    }

    private void showPlayerInfo(){
        lbPlayer1Role.setText(player1.getRole().name());
        lbPlayer1Points.setText(String.valueOf(player1.getNumberOfPoints()));
        lbPlayer2Role.setText(player2.getRole().name());
        lbPlayer2Points.setText(String.valueOf(player2.getNumberOfPoints()));
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

    private void enableCodeCircles(boolean isEnable){
        for(var codeCircle : codeCircles){
            codeCircle.setDisable(!isEnable);
        }
    }

    public void startGame(){
        numberOfRounds = spNumberOfRounds.getValue();
        enableCodeCircles(true);
        btnSetCode.setVisible(true);
    }

    private boolean isValidCode(){
        boolean isValid = true;

        for(var codeCircle : codeCircles){
            if(codeCircle.getFill().equals(defaultCircleColor)){
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    public void startGuessing(){
        if(!isValidCode()){
            DialogUtils.showInvalidCodeWarning();
            return;
        }

        enableCodeCircles(false);
        currentRow.setActive(true);

        btnSetCode.setVisible(false);
        btnNextRow.setVisible(true);
    }

    public void nextRow(){
        if(numberOfRounds < 1){
            if(player1.getNumberOfPoints() == player2.getNumberOfPoints()){
                DialogUtils.showGameResult("Draw", "It's draw!");
            }else{
                DialogUtils.showGameResult("Winner","Winner is " + (player1.getNumberOfPoints() > player2.getNumberOfPoints() ? "Player 1!" : "Player 2!"));
            }

            return;
        }

        currentRow.setActive(false);

        if(player1.getRole() == Role.Codemaker){
            player1.incrementPoints();
            lbPlayer1Points.setText(String.valueOf(player1.getNumberOfPoints()));
        }else{
            player2.incrementPoints();
            lbPlayer2Points.setText(String.valueOf(player2.getNumberOfPoints()));
        }

        int currentRowIndex = codeGuessRows.indexOf(currentRow);

        if(currentRowIndex + 1 == NUM_OF_GUESS_ROWS){
            numberOfRounds--;
            nextRound();
            return;
        }

        currentRow = codeGuessRows.get(currentRowIndex + 1);

        currentRow.setActive(true);
    }

    private void nextRound(){
        resetGuessRows();
        switchPlayerRoles();
        currentRow.setActive(false);
        currentRow = codeGuessRows.get(0);
        currentRow.setActive(true);
    }

    private void switchPlayerRoles(){
        player1.changeRole();
        player2.changeRole();

        lbPlayer1Role.setText(player1.getRole().name());
        lbPlayer2Role.setText((player2.getRole().name()));
    }

    private void resetGuessRows(){
        for(var guessRow : codeGuessRows){
            guessRow.resetRow();
        }
    }

    private void resetCodeCircles(){
        for(var codeCircle : codeCircles){
            codeCircle.setFill(defaultCircleColor);
        }
    }

    public void newGame(){
        player1.reset();
        player2.reset();
        showPlayerInfo();
        resetCodeCircles();
        enableCodeCircles(false);
        resetGuessRows();
        currentRow.setActive(false);
        currentRow = codeGuessRows.get(0);
        btnNextRow.setVisible(false);
        btnSetCode.setVisible(false);
        btnStartGame.setVisible(true);
        spNumberOfRounds.setValueFactory(spinnerValueFactory);
    }
}
