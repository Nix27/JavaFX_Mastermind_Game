package hr.algebra.mastermind.model;

import hr.algebra.mastermind.enums.MoveType;

import java.io.Serializable;

public class GameMove implements Serializable {

    public GameMove(MoveType moveType, String color) {
        this.moveType = moveType;
        this.color = color;
    }

    private MoveType moveType;
    private int rowIndex = -1;
    private String color;

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
