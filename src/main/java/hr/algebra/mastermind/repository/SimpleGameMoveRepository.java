package hr.algebra.mastermind.repository;

import hr.algebra.mastermind.model.GameMove;

import java.util.List;

public interface SimpleGameMoveRepository {
    void saveNewGameMove(GameMove newGameMove);
    List<GameMove> getAllGameMoves();
}
