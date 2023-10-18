package hr.algebra.mastermind.model;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public final class CodeGuessRow {
    private final BackgroundFill activeBackgroundFill = new BackgroundFill(Color.web("#4E4E4E"), CornerRadii.EMPTY, Insets.EMPTY);
    private final Background activeBackground = new Background(activeBackgroundFill);
    private final BackgroundFill defaultBackgroundFill = new BackgroundFill(Color.web("#373636"), CornerRadii.EMPTY, Insets.EMPTY);
    private final Background defaultBackground = new Background(defaultBackgroundFill);
    private final Paint defaultCircleColor = Color.web("#848484");

    private final HBox parent;
    private final List<Circle> guessCircles;
    private final List<Circle> hintCircles;

    public CodeGuessRow(HBox hBox) {
        parent = hBox;
        guessCircles = new ArrayList<>();
        hintCircles = new ArrayList<>();

        for(var node : hBox.getChildren()){
            if(node instanceof Circle guessCircle){
                guessCircle.setDisable(true);
                guessCircles.add(guessCircle);
            }

            if(node instanceof FlowPane hintCirclesFlowPane){
                for(var hintCircle : hintCirclesFlowPane.getChildren()){
                    if(hintCircle instanceof Circle hc){
                        hc.setDisable(true);
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

    public void setActiveGuessCircles(boolean isActive){
        if(isActive)
            parent.setBackground(activeBackground);

        for(var guessCircle : guessCircles){
            guessCircle.setDisable(!isActive);
        }
    }

    public void setActiveHintCircles(boolean isActive){
        if(!isActive)
            parent.setBackground(defaultBackground);

        for(var hintCircle : hintCircles){
            hintCircle.setDisable(!isActive);
        }
    }

    public void resetRow(){
        for(var guessCircle : guessCircles){
            guessCircle.setFill(defaultCircleColor);
        }

        for(var hintCircle : hintCircles){
            hintCircle.setFill(defaultCircleColor);
        }
    }
}
