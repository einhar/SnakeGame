package com.ehr.tetris;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tetromino {

    public int x;
    public int y;
    public Color color;
    public List<Piece> pieces;

    public Tetromino(Color color, Piece... pieces) {
        this.color = color;
        this.pieces = new ArrayList<>(Arrays.asList(pieces));

        for (Piece piece : this.pieces) piece.setParent(this);
    }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;

        pieces.forEach( piece -> {
            piece.x += x;
            piece.y += y;
        });
    }

    public void move(Direction direction) {
        move(direction.x, direction.y);
    }

}
