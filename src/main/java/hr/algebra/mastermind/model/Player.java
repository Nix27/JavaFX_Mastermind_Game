package hr.algebra.mastermind.model;

import hr.algebra.mastermind.enums.Role;

public final class Player {
    private int numberOfPoints = 0;
    private Role role;
    private final Role defaultRole;

    public Player(Role role){
        this.role = role;
        defaultRole = role;
    }

    public void incrementPoints(){
        numberOfPoints += 1;
    }

    public Role getRole() {
        return role;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void changeRole(){
        role = role == Role.Codemaker ? Role.Codebreaker : Role.Codemaker;
    }

    public void reset(){
        role = defaultRole;
        numberOfPoints = 0;
    }
}
