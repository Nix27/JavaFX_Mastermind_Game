package hr.algebra.mastermind.thread;

import hr.algebra.mastermind.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {

    private final Label lbLastGameMove;

    public GetLastGameMoveThread(Label lbLastGameMove){
        this.lbLastGameMove = lbLastGameMove;
    }

    @Override
    public void run() {
        while (true){
            GameMove lastGameMove = getLastGameMove();

            Platform.runLater(() -> lbLastGameMove.setText("The last game move: \nType: " + lastGameMove.getMoveType().name() +
                                                           (lastGameMove.getRowIndex() > -1 ? "\nRow: " + (lastGameMove.getRowIndex() + 1) : "") +
                                                           "\nCircle: " + (lastGameMove.getCircleIndex() + 1) +
                                                            "\nColor: " + lastGameMove.getColor()));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
