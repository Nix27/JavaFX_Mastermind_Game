package hr.algebra.mastermind.model;

import hr.algebra.mastermind.enums.MoveType;

import java.io.Serializable;

public class GameMove implements Serializable {

    public GameMove(MoveType moveType, int circleIndex, String color, boolean isVisiblePlayer1Indicator, boolean isVisiblePlayer2Indicator, int pointsPlayer1, int pointsPlayer2, String player1Role, String player2Role) {
        this.moveType = moveType;
        this.circleIndex = circleIndex;
        this.color = color;
        this.isVisiblePlayer1Indicator = isVisiblePlayer1Indicator;
        this.isVisiblePlayer2Indicator = isVisiblePlayer2Indicator;
        this.pointsPlayer1 = pointsPlayer1;
        this.pointsPlayer2 = pointsPlayer2;
        this.player1Role = player1Role;
        this.player2Role = player2Role;
    }

    private MoveType moveType;
    private int rowIndex = -1;
    private int circleIndex;
    private String color;
    private boolean isVisiblePlayer1Indicator;
    private boolean isVisiblePlayer2Indicator;
    private int pointsPlayer1;
    private int pointsPlayer2;
    private String player1Role;
    private String player2Role;

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

    public int getCircleIndex(){
        return circleIndex;
    }

    public void setCircleIndex(int circleIndex){
        this.circleIndex = circleIndex;
    }

    public boolean isVisiblePlayer1Indicator() { return isVisiblePlayer1Indicator; }
    public void setVisiblePlayer1Indicator(boolean isVisiblePlayer1Indicator) { this.isVisiblePlayer1Indicator = isVisiblePlayer1Indicator; }

    public boolean isVisiblePlayer2Indicator() { return isVisiblePlayer2Indicator; }
    public void setVisiblePlayer2Indicator(boolean isVisiblePlayer2Indicator) { this.isVisiblePlayer2Indicator = isVisiblePlayer2Indicator; }
    public int getPointsPlayer1() { return pointsPlayer1; }
    public void setPointsPlayer1(int pointsPlayer1) { this.pointsPlayer1 = pointsPlayer1; }
    public int getPointsPlayer2() { return pointsPlayer2; }
    public void setPointsPlayer2(int pointsPlayer2) { this.pointsPlayer2 = pointsPlayer2; }
    public String getPlayer1Role() { return player1Role; }
    public String getPlayer2Role() { return player2Role; }
}
