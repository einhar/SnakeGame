package com.ehr.tetris;

public class Piece {

    public int x;
    public int y;
    public int distance;
    public Direction direction;
    public Tetromino parent;

    public Piece(int distance, Direction direction) {
        this.distance = distance;
        this.direction = direction;
    }

    public void setParent(Tetromino parent) {
        this.parent = parent;
        this.x = parent.x + distance * direction.x;
        this.y = parent.y + distance * direction.y;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        this.x = parent.x + distance * direction.x;
        this.y = parent.y + distance * direction.y;
    }

    public Piece copy() {
        return new Piece(distance, direction);
    }
}
