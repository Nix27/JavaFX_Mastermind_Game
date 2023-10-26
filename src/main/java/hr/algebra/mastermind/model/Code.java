package hr.algebra.mastermind.model;

import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Code {
    private final Paint defaultCircleColor = Color.web("#848484");

    private final List<Circle> codeCircles = new ArrayList<>();

    public Code(HBox parent){
        for(var node : parent.getChildren()){
            if(node instanceof Circle codeCircle){
                codeCircle.setDisable(true);
                codeCircles.add(codeCircle);
            }
        }
    }

    public List<Circle> getCodeCircles() {
        return new ArrayList<>(codeCircles);
    }

    public void setVisible(boolean isVisible){
        codeCircles.forEach(c -> c.setVisible(isVisible));
    }

    public void resetCode(){
        for(var codeCircle : codeCircles){
            codeCircle.setFill(defaultCircleColor);
            codeCircle.setVisible(true);
        }
    }

    public boolean checkForDuplicates(Paint desiredColor){
        for(var codeCircle : codeCircles){
            if(codeCircle.getFill().equals(desiredColor)){
                return false;
            }
        }

        return true;
    }

    private List<String> getCodeColors(){
        return codeCircles
                .stream()
                .map(c -> c.getFill().toString())
                .collect(Collectors.toList());
    }
}
