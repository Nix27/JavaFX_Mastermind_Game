package hr.algebra.mastermind.model;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public final class CodeGuessRow {
    private final List<Circle> guessCircles;
    private final List<Circle> hintCircles;

    public CodeGuessRow(HBox hBox) {
        guessCircles = new ArrayList<>();
        hintCircles = new ArrayList<>();

        for(var node : hBox.getChildren()){
            if(node instanceof Circle guessCircle){
                guessCircles.add(guessCircle);
            }

            if(node instanceof FlowPane hintCirclesFlowPane){
                for(var hintCircle : hintCirclesFlowPane.getChildren()){
                    if(hintCircle instanceof Circle hc){
                        hintCircles.add(hc);
                    }
                }
            }
        }
    }

    public List<Circle> getGuessCircles() {
        return guessCircles;
    }

    public List<Circle> getHintCircles() {
        return hintCircles;
    }
}
