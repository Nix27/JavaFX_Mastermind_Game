package hr.algebra.mastermind.controller;

import hr.algebra.mastermind.model.CodeGuessRow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class MastermindController {

    public FlowPane guessColorsFlowPane;
    public FlowPane hintColorsFlowPane;
    public HBox codeHBox;
    public VBox guessRowsVBox;

    private Paint selectedColor;
    private Paint selectedHintColor;

    private final List<Circle> codeCircles = new ArrayList<>();
    private final List<CodeGuessRow> codeGuessRows = new ArrayList<>();

    public void initialize(){
        addEventToGuessColorCircles();
        addEventToHintColorCircles();
        initCodeCircles();
        initCodeGuessRows();
        addEventToGuessCircles();
        addEventToHintCircles();
    }

    private void initCodeCircles() {
        for(var node : codeHBox.getChildren()){
            if(node instanceof Circle codeCircle){
                codeCircle.setOnMouseClicked(e -> {
                    if(selectedColor != null){
                        codeCircle.setFill(selectedColor);
                    }
                });

                codeCircle.setDisable(true);
                codeCircles.add(codeCircle);
            }
        }
    }

    private void addEventToHintCircles() {
        for(var row : codeGuessRows) {
            for(var hintCircle : row.getHintCircles()){
                hintCircle.setOnMouseClicked(e -> {
                    if(selectedHintColor != null)
                        hintCircle.setFill(selectedHintColor);
                });
            }
        }
    }

    private void addEventToGuessCircles() {
        for(var row : codeGuessRows) {
            for(var guessCircle : row.getGuessCircles()){
                guessCircle.setOnMouseClicked(e -> {
                    if(selectedColor != null)
                        guessCircle.setFill(selectedColor);
                });
            }
        }
    }

    private void initCodeGuessRows() {
        for(var node : guessRowsVBox.getChildren()){
            if(node instanceof HBox newRow){
                codeGuessRows.add(new CodeGuessRow(newRow));
            }
        }
    }

    private void addEventToGuessColorCircles(){
        for(var node : guessColorsFlowPane.getChildren()){
            if(node instanceof Circle colorCircle){
                colorCircle.setOnMouseClicked(e -> selectedColor = colorCircle.getFill());
            }
        }
    }

    private void addEventToHintColorCircles(){
        for(var node: hintColorsFlowPane.getChildren()){
            if(node instanceof Circle hintColorCircle){
                hintColorCircle.setOnMouseClicked(e -> selectedHintColor = hintColorCircle.getFill());
            }
        }
    }
}
