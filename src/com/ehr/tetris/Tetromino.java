package com.ehr.tetris;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ehr.tetris.TetrisApp.TILE_SIZE;

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

    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(color);
        pieces.forEach( piece -> graphicsContext.fillRect(piece.x * TILE_SIZE, piece.y * TILE_SIZE, TILE_SIZE, TILE_SIZE));
    }

    public void rotateRight() {
        pieces.forEach( piece -> piece.setDirection(piece.direction.next()));
    }

    public void rotateLeft() {
        pieces.forEach( piece -> piece.setDirection(piece.direction.prev()));
    }

    public void detach(int x, int y) {
        pieces.removeIf(piece -> piece.x == x && piece.y == y);
    }

    public Tetromino copy() {
        return new Tetromino(color, pieces.stream().map(Piece::copy).collect(Collectors.toList()).toArray(new Piece[0]));
    }

}
