package hr.algebra.mastermind.model;

import hr.algebra.mastermind.enums.Role;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Player {
    private final IntegerProperty numberOfPoints = new SimpleIntegerProperty(0);
    private final Role role;

    public Player(Role role){
        this.role = role;
    }

    public void increasePoints(int increaseAmount){
        numberOfPoints.add(increaseAmount);
    }

    public Role getRole() {
        return role;
    }
}
