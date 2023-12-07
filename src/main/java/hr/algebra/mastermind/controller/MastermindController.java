package hr.algebra.mastermind.controller;

import hr.algebra.mastermind.MastermindApplication;
import hr.algebra.mastermind.chat.RemoteChatService;
import hr.algebra.mastermind.enums.NetworkRole;
import hr.algebra.mastermind.enums.Role;
import hr.algebra.mastermind.model.Code;
import hr.algebra.mastermind.model.CodeGuessRow;
import hr.algebra.mastermind.model.GameState;
import hr.algebra.mastermind.model.Player;
import hr.algebra.mastermind.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public Label lbResult;
    public TextField tfChatMessage;
    public  TextArea taChatMessages;
    public Button btnSend;

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
    public static RemoteChatService remoteChatService;

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

        setCodeVisibility();

        showPlayerInfo();
        player1Indicator.setVisible(false);
        player2Indicator.setVisible(false);

        spNumberOfRounds.setValueFactory(spinnerValueFactory);
        btnNextTurn.setVisible(false);

        if(MastermindApplication.loggedInNetworkRole.equals(NetworkRole.SERVER)){
            apStartGame.setVisible(true);
            RmiUtils.startRmiChatServer();

        }else {
            apStartGame.setVisible(false);
            RmiUtils.startRmiChatClient();
        }

        btnSend.setDisable(true);
        tfChatMessage.setOnAction(e -> {
            if(!tfChatMessage.getText().isBlank()){
                ChatUtils.sendChatMessage(tfChatMessage.getText());
                tfChatMessage.clear();
            }
        });
        tfChatMessage.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSend.setDisable(newValue.isBlank());
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> ChatUtils.refreshChatMessages(taChatMessages)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
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

        sendGameState(createGameState());
    }

    public void startGuessing() {
        if (!isValidCircles(code.getCodeCircles())) {
            DialogUtils.showWarning("Invalid code", "Code is not valid", "All circles of code should be filled with color!");
            return;
        }

        currentRow = codeGuessRows.get(0);
        currentRow.setActiveGuessCircles(true);
        currentTurn = Role.Codebreaker;
        setPlayerIndicator();

        btnSetCode.setVisible(false);
        btnNextTurn.setVisible(true);
        btnNextTurn.setDisable(true);

        updateDescriptionOfCurrentTurn(CODEBREAKER_GUESS);

        sendGameState(createGameState());
        enableGuessRows(false);
        enableCircles(false, code.getCodeCircles());
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
                nextRound();
                GameState gameState = createGameState();
                gameState.setIsCodeCorrect(true);
                if(numberOfRounds > 0){
                    gameState.setIsNextRound(true);
                }
                sendGameState(gameState);
                DialogUtils.showInfo("Code cracked", "Code is successfully cracked!");
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

        sendGameState(createGameState());
        enableGuessRows(false);
        btnNextTurn.setDisable(true);
    }

    public void newGame() {
        player1.reset();
        player2.reset();
        showPlayerInfo();
        currentTurn = null;
        setPlayerIndicator();
        code.resetCode();
        setCodeVisibility();
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
        lbResult.setText("");

        showStartGameWindow(true);

        sendGameState(createGameState());
    }

    public void saveGame() {
        var gameStateToSave = createGameState();

        try {
            FileUtils.save(gameStateToSave, GAME_STATE_FILE);
        } catch (IOException e) {
            DialogUtils.showErrorDialog("Save error", "Unable to save game!");
            e.printStackTrace();
        }
    }

    public void loadGame() {
        try {
            GameState loadedGameState = FileUtils.read(GAME_STATE_FILE);
            loadGameState(loadedGameState);
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showErrorDialog("Load error", "Unable to load game!");
            e.printStackTrace();
        }
    }

    public void loadGameState(GameState gameState){
        if (currentRow != null) {
            currentRow.setActiveGuessCircles(false);
            currentRow.setActiveHintCircles(false);
        }

        for(var codeCircle: code.getCodeCircles()){
            int indexOfColor = code.getCodeCircles().indexOf(codeCircle);
            codeCircle.setFill(Color.web(gameState.getCodeColors().get(indexOfColor)));
        }

        if(currentTurn != null && !currentTurn.equals(gameState.getCurrentTurn())){
            enableGuessRows(true);
            btnNextTurn.setDisable(false);
        }
        currentTurn = gameState.getCurrentTurn();

        if(gameState.isCodeCorrect()){
            DialogUtils.showInfo("Code cracked", "Code is successfully cracked!");
        }

        if(gameState.isNextRound()){
            codeHBox.setVisible(!codeHBox.isVisible());
            resetGuessRows();
        } else {
            for(var codeGuessRow : codeGuessRows){
                int indexOfRow = codeGuessRows.indexOf(codeGuessRow);
                List<String> colorsOfGuessCircles = gameState.getColorsOfGuessCircles().get(indexOfRow);
                List<String> colorsOfHintCircles = gameState.getColorsOfHintCircles().get(indexOfRow);

                for(int i = 0; i < colorsOfGuessCircles.size(); i++){
                    codeGuessRow.getGuessCircles().get(i).setFill(Color.web(colorsOfGuessCircles.get(i)));
                    codeGuessRow.getHintCircles().get(i).setFill(Color.web(colorsOfHintCircles.get(i)));
                }
            }
        }

        selectedColor = gameState.getSelectedColor();
        selectedHintColor = gameState.getSelectedHintColor();

        enableCircles(!gameState.getIsColorsDisabled(), colorCircles);
        enableCircles(!gameState.getIsHintColorsDisabled(), hintColorCircles);

        player1 = gameState.getPlayer1();
        player2 = gameState.getPlayer2();
        showPlayerInfo();

        numberOfRounds = gameState.getNumberOfRounds();

        if(currentTurn == null){
            setPlayerIndicator();
            resetGuessRows();
            lbResult.setText("");
            lbDescriptionOfCurrentTurn.setText("");
            showStartGameWindow(true);
        }

        if (!gameState.getDescriptionOfCurrentTurn().isBlank()) {
            lbDescriptionOfCurrentTurn.setText(gameState.getDescriptionOfCurrentTurn());
            setPlayerIndicator();
        }

        if(!gameState.getResult().isBlank()){
            lbResult.setText(gameState.getResult());
            btnNextTurn.setDisable(true);
        }

        btnNextTurn.setVisible(gameState.getIsBtnNextTurnVisible());

        if (gameState.getIndexOfCurrentRow() != -1) {
            currentRow = codeGuessRows.get(gameState.getIndexOfCurrentRow());
            if(currentTurn.equals(Role.Codemaker)){
                currentRow.setActiveHintCircles(true);
            }else {
                currentRow.setActiveGuessCircles(true);
            }

            setPlayerIndicator();
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
                        sendGameState(createGameState());
                    } else {
                        DialogUtils.showWarning("Duplicates", "Color duplicates", "The code must have different colors!");
                    }
                }
            });
        }
    }

    private void sendGameState(GameState gameState){
        if(MastermindApplication.loggedInNetworkRole.name().equals(NetworkRole.CLIENT.name())){
            NetworkingUtils.sendGameStateToServer(gameState);
        }else {
            NetworkingUtils.sendGameStateToClient(gameState);
        }
    }

    private GameState createGameState(){
        return new GameState(
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
                btnNextTurn.isVisible(),
                colorCircles.get(0).isDisable(),
                hintColorCircles.get(0).isDisable(),
                lbResult.getText());
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
                    if (selectedHintColor != defaultCircleColor){
                        hintCircle.setFill(selectedHintColor);

                        sendGameState(createGameState());
                    }
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

                            sendGameState(createGameState());
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

    private static void enableCircles(boolean isEnable, List<Circle> circles) {
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
            if(!code.getCodeCircles().get(i).getFill().equals(currentRow.getGuessCircles().get(i).getFill())){
                isRightCode = false;
                break;
            }
        }

        return isRightCode;
    }

    private void setPlayerIndicator() {
        if(currentTurn == null){
            player1Indicator.setVisible(false);
            player2Indicator.setVisible(false);
            return;
        }

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
            GameState gameState = createGameState();
            gameState.setIsNextRound(true);
            sendGameState(gameState);
            return;
        }

        currentRow = codeGuessRows.get(currentRowIndex + 1);

        currentRow.setActiveGuessCircles(true);
    }

    private boolean checkIfLastRound() {
        if (numberOfRounds < 1) {
            if (player1.getNumberOfPoints() == player2.getNumberOfPoints()) {
                lbResult.setText("It's draw!");
            } else {
                lbResult.setText("Winner is " + (player1.getNumberOfPoints() > player2.getNumberOfPoints() ? "Player 1!" : "Player 2!"));
            }

            btnNextTurn.setDisable(true);

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

        codeHBox.setVisible(!codeHBox.isVisible());
    }

    private void resetGuessRows() {
        for (var guessRow : codeGuessRows) {
            guessRow.resetRow();
        }
    }

    private void setCodeVisibility(){
        if(MastermindApplication.loggedInNetworkRole.equals(NetworkRole.SERVER)){
            boolean isVisible = player1.getRole().equals(Role.Codemaker);
            codeHBox.setVisible(isVisible);
        }else {
            boolean isVisible = player2.getRole().equals(Role.Codemaker);
            codeHBox.setVisible(isVisible);
        }
    }

    private void enableGuessRows(boolean enable){
        guessRowsVBox.setDisable(!enable);
    }

    private void showStartGameWindow(boolean isShowed) {
        if (MastermindApplication.loggedInNetworkRole.equals(NetworkRole.SERVER)) {
            apStartGame.setVisible(isShowed);
        }
    }

    public void sendChatMessage(){
        ChatUtils.sendChatMessage(tfChatMessage.getText());
        tfChatMessage.clear();
    }
}