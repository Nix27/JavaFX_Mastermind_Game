package hr.algebra.mastermind.controller;

import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class MastermindController {

    public FlowPane colorsFlowPane;

    private List<Circle> colorCircles;

    private Paint selectedColor;

    public void initialize(){
        colorCircles = new ArrayList<>();

        for(var node : colorsFlowPane.getChildren()){
            if(node instanceof Circle colorCircle){
                colorCircle.setOnMouseClicked(e -> selectedColor = colorCircle.getFill());
            }
        }
    }
}
