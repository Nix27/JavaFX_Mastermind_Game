package hr.algebra.mastermind.model;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CodeGuessRow implements Serializable {
    private final BackgroundFill activeBackgroundFill = new BackgroundFill(Color.web("#4E4E4E"), CornerRadii.EMPTY, Insets.EMPTY);
    private final Background activeBackground = new Background(activeBackgroundFill);
    private final BackgroundFill defaultBackgroundFill = new BackgroundFill(Color.web("#373636"), CornerRadii.EMPTY, Insets.EMPTY);
    private final Background defaultBackground = new Background(defaultBackgroundFill);
    private final Paint defaultCircleColor = Color.web("#848484");

    private HBox parent;
    private List<Circle> guessCircles;
    private List<Circle> hintCircles;

    public CodeGuessRow () {}

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
        return new ArrayList<>(guessCircles);
    }

    public List<Circle> getHintCircles() {
        return new ArrayList<>(hintCircles);
    }

    public void setActiveGuessCircles(boolean isActive){
        if(isActive)
            parent.setBackground(activeBackground);

        for(var guessCircle : guessCircles){
            guessCircle.setDisable(!isActive);
        }
    }

    public void setActiveHintCircles(boolean isActive){
        for(var hintCircle : hintCircles){
            hintCircle.setDisable(!isActive);
        }
    }

    public void resetRow(){
        parent.setBackground(defaultBackground);

        for(var guessCircle : guessCircles){
            guessCircle.setFill(defaultCircleColor);
        }

        for(var hintCircle : hintCircles){
            hintCircle.setFill(defaultCircleColor);
        }
    }

    public boolean checkForDuplicatesInGuess(Paint desiredColor){
        for(var guessCircle : guessCircles){
            if(guessCircle.getFill().equals(desiredColor)){
                return false;
            }
        }

        return true;
    }

    public List<String> getGuessColors(){
        return guessCircles
                .stream()
                .map(gc -> gc.getFill().toString())
                .collect(Collectors.toList());
    }

    public List<String> getHintColors(){
        return hintCircles
                .stream()
                .map(hc -> hc.getFill().toString())
                .collect(Collectors.toList());
    }
}
