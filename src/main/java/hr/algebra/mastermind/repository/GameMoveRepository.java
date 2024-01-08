package hr.algebra.mastermind.repository;

import hr.algebra.mastermind.model.GameMove;

public interface GameMoveRepository extends SimpleGameMoveRepository {
    GameMove getLastGameMove();
}
