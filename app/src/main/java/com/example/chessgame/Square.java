package com.example.chessgame;

public class Square {
    private int row;
    private int col;
    private int isEmpty = 1;

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getIsEmpty() {
        return isEmpty;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setIsEmpty(int isEmpty) {
        this.isEmpty = isEmpty;
    }

}
