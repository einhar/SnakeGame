package com.ehr.snake2;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import static com.ehr.snake2.Main.BLOCK_SIZE;

public class NewBody extends Pane {
    private Rectangle rOut1;
    public NewBody(double x, double y){
        this.setTranslateX(x);
        this.setTranslateY(y);
        rOut1 = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
        Image body = new Image("com/ehr/snake2/body.png");
        ImagePattern ip = new ImagePattern(body);
        rOut1.setFill(ip);
        super.getChildren().addAll(rOut1);
    }

    public void setrOut1(ImagePattern imp) {
        this.rOut1.setFill(imp);
    }
}