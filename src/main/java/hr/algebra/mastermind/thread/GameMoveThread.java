package hr.algebra.mastermind.thread;

import hr.algebra.mastermind.model.GameMove;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread {
    private static final String GAME_MOVES_FILE_NAME = "files/game_moves.dat";

    private static boolean inProgress = false;

    public synchronized GameMove getLastGameMove(){
        while(inProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        inProgress = true;

        GameMove lastGameMove = getAllGameMoves().getLast();

        inProgress = false;
        notifyAll();

        return lastGameMove;
    }

    public synchronized void saveNewGameMove(GameMove newGameMove){
        while(inProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        inProgress = true;

        List<GameMove> allGameMoves = getAllGameMoves();
        allGameMoves.add(newGameMove);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_MOVES_FILE_NAME))){
            oos.writeObject(allGameMoves);
        } catch (IOException e) {
            e.printStackTrace();
        }

        inProgress = false;
        notifyAll();
    }

    private synchronized List<GameMove> getAllGameMoves(){
        List<GameMove> allGameMoves = new ArrayList<>();

        if(Files.exists(Path.of(GAME_MOVES_FILE_NAME))){
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GAME_MOVES_FILE_NAME))){
                allGameMoves.addAll((List<GameMove>)ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return allGameMoves;
    }
}
