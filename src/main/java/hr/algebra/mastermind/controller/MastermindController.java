package hr.algebra.mastermind.controller;

import hr.algebra.mastermind.enums.Role;
import hr.algebra.mastermind.model.CodeGuessRow;
import hr.algebra.mastermind.model.GameState;
import hr.algebra.mastermind.model.Player;
import hr.algebra.mastermind.utils.DialogUtils;
import hr.algebra.mastermind.utils.FileUtils;
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

import java.io.IOException;
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
    public Button btnNextTurn;
    public Label lbPlayer1Role;
    public Label lbPlayer1Points;
    public Label lbPlayer2Role;
    public Label lbPlayer2Points;
    public Spinner<Integer> spNumberOfRounds;

    private final Paint defaultCircleColor = Color.web("#848484");
    private Paint selectedColor;
    private Paint selectedHintColor;

    private List<Circle> colorCircles = new ArrayList<>();
    private List<Circle> hintColorCircles = new ArrayList<>();
    private List<Circle> codeCircles = new ArrayList<>();
    private List<CodeGuessRow> codeGuessRows = new ArrayList<>();

    private CodeGuessRow currentRow;

    private int numberOfRounds;
    private Player player1;
    private Player player2;
    private Role currentTurn;

    private final SpinnerValueFactory<Integer> spinnerValueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 2, 2);

    public void initialize(){
        initColorCircles();
        initHintColorCircles();
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
        btnNextTurn.setVisible(false);
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

    private void initColorCircles(){
        for(var node : guessColorsFlowPane.getChildren()){
            if(node instanceof Circle colorCircle){
                colorCircle.setOnMouseClicked(e -> selectedColor = colorCircle.getFill());
                colorCircle.setDisable(true);
                colorCircles.add(colorCircle);
            }
        }
    }

    private void initHintColorCircles(){
        for(var node: hintColorsFlowPane.getChildren()){
            if(node instanceof Circle hintColorCircle){
                hintColorCircle.setOnMouseClicked(e -> selectedHintColor = hintColorCircle.getFill());
                hintColorCircle.setDisable(true);
                hintColorCircles.add(hintColorCircle);
            }
        }
    }

    private void enableColorCircles(boolean isEnable){
        for(var colorCircle : colorCircles){
            colorCircle.setDisable(!isEnable);
        }
    }

    private void enableHintColorCircles(boolean isEnable){
        for(var hintColorCircle : hintColorCircles){
            hintColorCircle.setDisable(!isEnable);
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
        enableColorCircles(true);
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
        currentRow.setActiveGuessCircles(true);
        currentTurn = Role.Codebreaker;

        btnSetCode.setVisible(false);
        btnNextTurn.setVisible(true);
    }

    public void nextTurn(){
        if(numberOfRounds < 1){
            if(player1.getNumberOfPoints() == player2.getNumberOfPoints()){
                DialogUtils.showGameResult("Draw", "It's draw!");
            }else{
                DialogUtils.showGameResult("Winner","Winner is " + (player1.getNumberOfPoints() > player2.getNumberOfPoints() ? "Player 1!" : "Player 2!"));
            }

            return;
        }

        if(currentTurn == Role.Codemaker){
            nextRow();
            currentTurn = Role.Codebreaker;
            enableColorCircles(true);
            enableHintColorCircles(false);
        }else{
            enableColorCircles(false);
            enableHintColorCircles(true);
            currentRow.setActiveGuessCircles(false);
            currentRow.setActiveHintCircles(true);
            currentTurn = Role.Codemaker;
        }
    }

    private void nextRow(){
        currentRow.setActiveHintCircles(false);

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

        currentRow.setActiveGuessCircles(true);
    }

    private void nextRound(){
        resetGuessRows();
        switchPlayerRoles();
        currentRow.setActiveGuessCircles(false);
        currentRow = codeGuessRows.get(0);
        currentRow.setActiveGuessCircles(true);
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
        currentRow.setActiveGuessCircles(false);
        currentRow = codeGuessRows.get(0);
        btnNextTurn.setVisible(false);
        btnSetCode.setVisible(false);
        btnStartGame.setVisible(true);
        spNumberOfRounds.setValueFactory(spinnerValueFactory);
    }

    public void saveGame(){
        var gameStateToSave = new GameState(
                selectedColor,
                selectedHintColor,
                codeCircles,
                codeGuessRows,
                codeGuessRows.indexOf(currentRow),
                numberOfRounds,
                player1,
                player2,
                currentTurn,
                btnSetCode.isVisible(),
                btnNextTurn.isVisible());

        try {
            FileUtils.save(gameStateToSave, "gameState.ser");
        } catch (IOException e) {
            DialogUtils.showErrorDialog("Save error", "Unable to save game!");
            e.printStackTrace();
        }
    }

    public void loadGame(){
        try {
            GameState loadedGameState = FileUtils.read("gameState.ser");

            for(var colorCode : loadedGameState.getCodeColors()){
                int indexOfCircle = loadedGameState.getCodeColors().indexOf(colorCode);
                codeCircles.get(indexOfCircle).setFill(Color.web(colorCode));
            }

            for(var guessColorsOfRow : loadedGameState.getColorsOfGuessCircles()){
                int indexOfRow = loadedGameState.getColorsOfGuessCircles().indexOf(guessColorsOfRow);
                List<Circle> guessCircles = codeGuessRows.get(indexOfRow).getGuessCircles();

                for(int i = 0; i < guessColorsOfRow.size(); i++){
                    guessCircles.get(i).setFill(Color.web(guessColorsOfRow.get(i)));
                }
            }

            for(var hintColorsOfRow : loadedGameState.getColorsOfHintCircles()){
                int indexOfRow = loadedGameState.getColorsOfHintCircles().indexOf(hintColorsOfRow);
                List<Circle> hintCircles = codeGuessRows.get(indexOfRow).getHintCircles();

                for(int i = 0; i < hintColorsOfRow.size(); i++){
                    hintCircles.get(i).setFill(Color.web(hintColorsOfRow.get(i)));
                }
            }

            selectedColor = loadedGameState.getSelectedColor();
            selectedHintColor = loadedGameState.getSelectedHintColor();
            currentRow = codeGuessRows.get(loadedGameState.getIndexOfCurrentRow());
            numberOfRounds = loadedGameState.getNumberOfRounds();
            currentTurn = loadedGameState.getCurrentTurn();
            player1 = loadedGameState.getPlayer1();
            player2 = loadedGameState.getPlayer2();

            showPlayerInfo();
            btnSetCode.setVisible(loadedGameState.getIsBtnSetCodeVisible());
            btnNextTurn.setVisible(loadedGameState.getIsBtnNextTurnVisible());
            currentRow.setActiveGuessCircles(true);
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showErrorDialog("Load error", "Unable to load game!");
            e.printStackTrace();
        }
    }
}
