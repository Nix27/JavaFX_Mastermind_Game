package hr.algebra.mastermind.thread;

import hr.algebra.mastermind.model.GameMove;
import hr.algebra.mastermind.model.GameState;

public class SaveNewGameMoveThread extends GameMoveThread implements Runnable {

    private final GameMove newGameMove;

    public SaveNewGameMoveThread(GameMove newGameMove){
        this.newGameMove = newGameMove;
    }

    @Override
    public void run() {
        saveNewGameMove(newGameMove);
    }
}
