package hr.algebra.mastermind.controller;

import hr.algebra.mastermind.enums.Role;
import hr.algebra.mastermind.model.Code;
import hr.algebra.mastermind.model.CodeGuessRow;
import hr.algebra.mastermind.model.GameState;
import hr.algebra.mastermind.model.Player;
import hr.algebra.mastermind.utils.DialogUtils;
import hr.algebra.mastermind.utils.DocumentationUtils;
import hr.algebra.mastermind.utils.FileUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MastermindController {
    private final int NUM_OF_GUESS_ROWS = 10;
    private final String CODEMAKER_SETS_CODE = "Codemaker sets the code";
    private final String CODEBREAKER_GUESS = "Codebreaker guesses the code";
    private final String CODEMAKER_GIVES_HINT = "Codemaker gives a hint";
    private final String GAME_STATE_FILE = "gameState.ser";

    public AnchorPane apStartGame;
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
    public Circle player1Indicator;
    public Circle player2Indicator;
    public Label lbDescriptionOfCurrentTurn;

    private final Paint defaultCircleColor = Color.web("#848484");
    private Paint selectedColor;
    private Paint selectedHintColor;

    private final List<Circle> colorCircles = new ArrayList<>();
    private final List<Circle> hintColorCircles = new ArrayList<>();
    private Code code;
    private final List<CodeGuessRow> codeGuessRows = new ArrayList<>();

    private CodeGuessRow currentRow;

    private int numberOfRounds;
    private Player player1;
    private Player player2;
    private Role currentTurn;

    private final SpinnerValueFactory<Integer> spinnerValueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 2, 2);

    public void initialize() {
        initColorCircles();
        initHintColorCircles();
        initCode();
        initCodeGuessRows();
        addEventToGuessCircles();
        addEventToHintCircles();

        selectedColor = defaultCircleColor;
        selectedHintColor = defaultCircleColor;

        player1 = new Player(Role.Codemaker);
        player2 = new Player(Role.Codebreaker);

        showPlayerInfo();
        player1Indicator.setVisible(false);
        player2Indicator.setVisible(false);

        spNumberOfRounds.setValueFactory(spinnerValueFactory);
        btnSetCode.setVisible(false);
        btnNextTurn.setVisible(false);
        lbDescriptionOfCurrentTurn.setVisible(false);
    }

    public void startGame() {
        numberOfRounds = spNumberOfRounds.getValue();
        enableCircles(true, code.getCodeCircles());
        btnSetCode.setVisible(true);
        enableCircles(true, colorCircles);
        apStartGame.setVisible(false);
        player1Indicator.setVisible(true);
        currentTurn = Role.Codemaker;
        lbDescriptionOfCurrentTurn.setVisible(true);
        updateDescriptionOfCurrentTurn(CODEMAKER_SETS_CODE);
    }

    public void startGuessing() {
        if (!isValidCircles(code.getCodeCircles())) {
            DialogUtils.showWarning("Invalid code", "Code is not valid", "All circles of code should be filled with color!");
            return;
        }

        code.setVisible(false);
        currentRow = codeGuessRows.get(0);
        currentRow.setActiveGuessCircles(true);
        currentTurn = Role.Codebreaker;
        setPlayerIndicator();

        btnSetCode.setVisible(false);
        btnNextTurn.setVisible(true);
        updateDescriptionOfCurrentTurn(CODEBREAKER_GUESS);
    }

    public void nextTurn() {
        if (checkIfLastRound()) return;

        if (currentTurn == Role.Codemaker) {
            currentTurn = Role.Codebreaker;
            updateDescriptionOfCurrentTurn(CODEBREAKER_GUESS);
            nextRow();
            enableCircles(true, colorCircles);
            enableCircles(false, hintColorCircles);

            setPlayerIndicator();
        } else {
            if (!isValidCircles(currentRow.getGuessCircles())) {
                DialogUtils.showWarning("Invalid guess", "Guess is not valid", "All circles of guess should be filled with color!");
                return;
            }

            if (checkForRightCode()) {
                code.setVisible(true);
                DialogUtils.showInfo("Code cracked", "Code is successfully cracked!");
                nextRound();
                return;
            }

            enableCircles(false, colorCircles);
            enableCircles(true, hintColorCircles);
            currentRow.setActiveGuessCircles(false);
            currentRow.setActiveHintCircles(true);
            currentTurn = Role.Codemaker;

            setPlayerIndicator();
            updateDescriptionOfCurrentTurn(CODEMAKER_GIVES_HINT);
        }
    }

    public void newGame() {
        player1.reset();
        player2.reset();
        showPlayerInfo();
        player1Indicator.setVisible(false);
        player2Indicator.setVisible(false);
        code.resetCode();
        enableCircles(false, code.getCodeCircles());
        resetGuessRows();

        if (currentRow != null) {
            currentRow.setActiveGuessCircles(false);
            currentRow.setActiveHintCircles(false);
            currentRow = null;
        }

        btnNextTurn.setVisible(false);
        btnSetCode.setVisible(false);
        btnStartGame.setVisible(true);
        spNumberOfRounds.setValueFactory(spinnerValueFactory);
        lbDescriptionOfCurrentTurn.setText("");
        apStartGame.setVisible(true);
    }

    public void saveGame() {
        var gameStateToSave = new GameState(
                selectedColor,
                selectedHintColor,
                code.getCodeCircles(),
                codeGuessRows,
                codeGuessRows.indexOf(currentRow),
                numberOfRounds,
                player1,
                player2,
                currentTurn,
                lbDescriptionOfCurrentTurn.getText(),
                apStartGame.isVisible(),
                btnSetCode.isVisible(),
                btnNextTurn.isVisible(),
                colorCircles.get(0).isDisable(),
                hintColorCircles.get(0).isDisable());

        try {
            FileUtils.save(gameStateToSave, GAME_STATE_FILE);
        } catch (IOException e) {
            DialogUtils.showErrorDialog("Save error", "Unable to save game!");
            e.printStackTrace();
        }
    }

    public void loadGame() {
        try {
            if (currentRow != null) {
                currentRow.setActiveGuessCircles(false);
                currentRow.setActiveHintCircles(false);
            }

            GameState loadedGameState = FileUtils.read(GAME_STATE_FILE);

            for (var colorCode : loadedGameState.getCodeColors()) {
                int indexOfCircle = loadedGameState.getCodeColors().indexOf(colorCode);
                code.getCodeCircles().get(indexOfCircle).setFill(Color.web(colorCode));
            }

            for (var guessColorsOfRow : loadedGameState.getColorsOfGuessCircles()) {
                int indexOfRow = loadedGameState.getColorsOfGuessCircles().indexOf(guessColorsOfRow);
                List<Circle> guessCircles = codeGuessRows.get(indexOfRow).getGuessCircles();

                for (int i = 0; i < guessColorsOfRow.size(); i++) {
                    guessCircles.get(i).setFill(Color.web(guessColorsOfRow.get(i)));
                }
            }

            for (var hintColorsOfRow : loadedGameState.getColorsOfHintCircles()) {
                int indexOfRow = loadedGameState.getColorsOfHintCircles().indexOf(hintColorsOfRow);
                List<Circle> hintCircles = codeGuessRows.get(indexOfRow).getHintCircles();

                for (int i = 0; i < hintColorsOfRow.size(); i++) {
                    hintCircles.get(i).setFill(Color.web(hintColorsOfRow.get(i)));
                }
            }

            selectedColor = loadedGameState.getSelectedColor();
            selectedHintColor = loadedGameState.getSelectedHintColor();

            enableCircles(!loadedGameState.getIsColorsDisabled(), colorCircles);
            enableCircles(!loadedGameState.getIsHintColorsDisabled(), hintColorCircles);

            numberOfRounds = loadedGameState.getNumberOfRounds();
            currentTurn = loadedGameState.getCurrentTurn();

            apStartGame.setVisible(loadedGameState.getIsStartGameVisible());
            player1 = loadedGameState.getPlayer1();
            player2 = loadedGameState.getPlayer2();
            showPlayerInfo();

            if (!loadedGameState.getDescriptionOfCurrentTurn().isBlank()) {
                enableCircles(true, code.getCodeCircles());
                lbDescriptionOfCurrentTurn.setText(loadedGameState.getDescriptionOfCurrentTurn());
                setPlayerIndicator();
            }

            btnSetCode.setVisible(loadedGameState.getIsBtnSetCodeVisible());
            btnNextTurn.setVisible(loadedGameState.getIsBtnNextTurnVisible());

            if (loadedGameState.getIndexOfCurrentRow() != -1) {
                code.setVisible(false);
                currentRow = codeGuessRows.get(loadedGameState.getIndexOfCurrentRow());
                currentRow.setActiveGuessCircles(true);
                setPlayerIndicator();
            } else {
                code.setVisible(true);
            }
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showErrorDialog("Load error", "Unable to load game!");
            e.printStackTrace();
        }
    }

    public void generateDocumentation() {
        DocumentationUtils.createHtmlDocumentation();
    }

    //private methods
    private void updateDescriptionOfCurrentTurn(String description) {
        lbDescriptionOfCurrentTurn.setText(description);
    }

    private void initCode() {
        code = new Code(codeHBox);

        for (var codeCircle : code.getCodeCircles()) {
            codeCircle.setOnMouseClicked(e -> {
                if (selectedColor != defaultCircleColor) {
                    if (code.checkForDuplicates(selectedColor)) {
                        codeCircle.setFill(selectedColor);
                    } else {
                        DialogUtils.showWarning("Duplicates", "Color duplicates", "The code must have different colors!");
                    }
                }
            });
        }
    }

    private void showPlayerInfo() {
        lbPlayer1Role.setText(player1.getRole().name());
        lbPlayer1Points.setText(String.valueOf(player1.getNumberOfPoints()));
        lbPlayer2Role.setText(player2.getRole().name());
        lbPlayer2Points.setText(String.valueOf(player2.getNumberOfPoints()));
    }

    private void addEventToHintCircles() {
        for (var row : codeGuessRows) {
            for (var hintCircle : row.getHintCircles()) {
                hintCircle.setOnMouseClicked(e -> {
                    if (selectedHintColor != defaultCircleColor)
                        hintCircle.setFill(selectedHintColor);
                });
            }
        }
    }

    private void addEventToGuessCircles() {
        for (var row : codeGuessRows) {
            for (var guessCircle : row.getGuessCircles()) {
                guessCircle.setOnMouseClicked(e -> {
                    if (selectedColor != defaultCircleColor) {
                        if (row.checkForDuplicatesInGuess(selectedColor)) {
                            guessCircle.setFill(selectedColor);
                        } else {
                            DialogUtils.showWarning("Duplicates", "Color duplicates", "The code must have different colors!");
                        }
                    }
                });
            }
        }
    }

    private void initCodeGuessRows() {
        for (var node : guessRowsVBox.getChildren()) {
            if (node instanceof HBox newRow) {
                codeGuessRows.add(new CodeGuessRow(newRow));
            }
        }
    }

    private void initColorCircles() {
        for (var node : guessColorsFlowPane.getChildren()) {
            if (node instanceof Circle colorCircle) {
                colorCircle.setOnMouseClicked(e -> selectedColor = colorCircle.getFill());
                colorCircle.setDisable(true);
                colorCircles.add(colorCircle);
            }
        }
    }

    private void initHintColorCircles() {
        for (var node : hintColorsFlowPane.getChildren()) {
            if (node instanceof Circle hintColorCircle) {
                hintColorCircle.setOnMouseClicked(e -> selectedHintColor = hintColorCircle.getFill());
                hintColorCircle.setDisable(true);
                hintColorCircles.add(hintColorCircle);
            }
        }
    }

    private void enableCircles(boolean isEnable, List<Circle> circles) {
        for (var circle : circles) {
            circle.setDisable(!isEnable);
        }
    }

    private boolean isValidCircles(List<Circle> circles) {
        boolean isValid = true;

        for (var circle : circles) {
            if (circle.getFill().equals(defaultCircleColor)) {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    private boolean checkForRightCode() {
        boolean isRightCode = true;

        for(int i = 0; i < code.getCodeCircles().size(); i++){
            if(code.getCodeCircles().get(i).getFill() != currentRow.getGuessCircles().get(i).getFill()){
                isRightCode = false;
                break;
            }
        }

        return isRightCode;
    }

    private void setPlayerIndicator() {
        if (player1.getRole() == currentTurn) {
            player1Indicator.setVisible(true);
            player2Indicator.setVisible(false);
        } else {
            player1Indicator.setVisible(false);
            player2Indicator.setVisible(true);
        }
    }

    private void nextRow() {
        currentRow.setActiveHintCircles(false);

        if (player1.getRole() == Role.Codemaker) {
            player1.incrementPoints();
            lbPlayer1Points.setText(String.valueOf(player1.getNumberOfPoints()));
        } else {
            player2.incrementPoints();
            lbPlayer2Points.setText(String.valueOf(player2.getNumberOfPoints()));
        }

        int currentRowIndex = codeGuessRows.indexOf(currentRow);

        if (currentRowIndex + 1 == NUM_OF_GUESS_ROWS) {
            nextRound();
            return;
        }

        currentRow = codeGuessRows.get(currentRowIndex + 1);

        currentRow.setActiveGuessCircles(true);
    }

    private boolean checkIfLastRound() {
        if (numberOfRounds < 1) {
            if (player1.getNumberOfPoints() == player2.getNumberOfPoints()) {
                DialogUtils.showInfo("Draw", "It's draw!");
            } else {
                DialogUtils.showInfo("Winner", "Winner is " + (player1.getNumberOfPoints() > player2.getNumberOfPoints() ? "Player 1!" : "Player 2!"));
            }

            return true;
        }

        return false;
    }

    private void nextRound() {
        numberOfRounds--;
        if (checkIfLastRound()) return;

        resetGuessRows();
        switchPlayerRoles();
        currentRow.setActiveHintCircles(false);
        enableCircles(true, code.getCodeCircles());
        btnSetCode.setVisible(true);
        enableCircles(true, colorCircles);
        currentRow = codeGuessRows.get(0);
        code.resetCode();
        btnNextTurn.setVisible(false);
        currentTurn = Role.Codemaker;
        setPlayerIndicator();
        updateDescriptionOfCurrentTurn(CODEMAKER_SETS_CODE);
    }

    private void switchPlayerRoles() {
        player1.changeRole();
        player2.changeRole();

        lbPlayer1Role.setText(player1.getRole().name());
        lbPlayer2Role.setText((player2.getRole().name()));
    }

    private void resetGuessRows() {
        for (var guessRow : codeGuessRows) {
            guessRow.resetRow();
        }
    }
}