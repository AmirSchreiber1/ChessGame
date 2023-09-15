package com.example.chessgame;

import java.util.ArrayList;

public class Pawn extends ChessPiece {
    private int isBottom;
    private boolean stillNotMoved;

    public Pawn(char color, Square currentSquare, int isBottom) {
        super(color, currentSquare);
        this.isBottom = isBottom;
        this.stillNotMoved = true;
    }


    public boolean getStillNotMoved() {
        return stillNotMoved;
    }

    public void setStillNotMoved(boolean stillNotMoved) {
        this.stillNotMoved = stillNotMoved;
    }

    @Override
    public ArrayList<Square> getPossibleSquares(String[][] board) {
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = this.currentSquare.getRow();
        int col = this.currentSquare.getCol();
        if (isBottom == 1) {
            //regular moving:
            if (row-1 >= 0 && board[row-1][col].equals("_")) {
                Square newSquare = new Square(row - 1, col);
                possibleSquares.add(newSquare);
            }
            if (row-1 >= 0 && stillNotMoved && board[row-1][col].equals("_")) {
                if (row-2 >= 0 && board[row-2][col].equals("_")) {
                    Square newSquare = new Square(row - 2, col);
                    possibleSquares.add(newSquare);
                }
            }
            //eating:
            if (row-1>=0 && col-1>=0 && (!(board[row-1][col-1].equals("_"))) && board[row-1][col-1].charAt(0) != this.getColor()) {
                Square newSquare = new Square(row - 1, col - 1);
                possibleSquares.add(newSquare);
            }
            if (row-1>=0 && col+1 < 8 && (!(board[row-1][col+1].equals("_"))) && board[row-1][col+1].charAt(0) != this.getColor()) {
                Square newSquare = new Square(row - 1, col + 1);
                possibleSquares.add(newSquare);
            }
        }
        else { //isUp
            //regular moving:
            if (row+1<8 && board[row+1][col].equals("_")) {
                Square newSquare = new Square(row + 1, col);
                possibleSquares.add(newSquare);
            }
            if (row+1<8 && stillNotMoved && board[row+1][col].equals("_")) {
                if (row+2<8 && board[row+2][col].equals("_")) {
                    Square newSquare = new Square(row + 2, col);
                    possibleSquares.add(newSquare);
                }
            }
            //eating:
            if (row+1<8 && col-1>=0 && (!(board[row+1][col-1].equals("_"))) && board[row+1][col-1].charAt(0) != this.getColor()) {
                Square newSquare = new Square(row + 1, col - 1);
                possibleSquares.add(newSquare);
            }
            if (row+1<8 && col+1<8 && (!(board[row+1][col+1].equals("_"))) && board[row+1][col+1].charAt(0) != this.getColor()) {
                Square newSquare = new Square(row + 1, col + 1);
                possibleSquares.add(newSquare);
            }
        }
        return possibleSquares;
    }
}
