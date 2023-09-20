package com.example.chessgame;

import java.util.ArrayList;

public class King extends ChessPiece {
    private boolean stillNotMoved;
    Rook rightRook;
    Rook leftRook;

    public King(char color, Square currentSquare) {
        super(color, currentSquare);
        this.stillNotMoved = true;
    }


    public boolean getStillNotMoved() {
        return stillNotMoved;
    }

    public void setStillNotMoved(boolean stillNotMoved) {
        this.stillNotMoved = stillNotMoved;
    }

    public void setLeftRook(Rook leftRook) {
        this.leftRook = leftRook;
    }

    public void setRightRook(Rook rightRook) {
        this.rightRook = rightRook;
    }

    @Override
    public ArrayList<Square> getPossibleSquares(String[][] board) {
        ArrayList<Square> possibleSquares = new ArrayList<>();
        int row = this.currentSquare.getRow();
        int col = this.currentSquare.getCol();
        checkAndAddPossibleSquare(board, possibleSquares, row + 1, col + 1);
        checkAndAddPossibleSquare(board, possibleSquares, row + 1, col);
        checkAndAddPossibleSquare(board, possibleSquares, row + 1, col - 1);
        checkAndAddPossibleSquare(board, possibleSquares, row, col + 1);
        checkAndAddPossibleSquare(board, possibleSquares, row, col - 1);
        checkAndAddPossibleSquare(board, possibleSquares, row - 1, col + 1);
        checkAndAddPossibleSquare(board, possibleSquares, row - 1, col);
        checkAndAddPossibleSquare(board, possibleSquares, row - 1, col - 1);
        if (this.stillNotMoved && rightRook!= null && rightRook.isAlive() && rightRook.getStillNotMoved()) {
            if (board[row][col+1].equals("_") && board[row][col+2].equals("_")) {
                //possible hatzraha:
                Square newSquare = new Square(row, col + 2);
                possibleSquares.add(newSquare);
            }
        }
        if (this.stillNotMoved && leftRook!= null && leftRook.isAlive() && leftRook.getStillNotMoved()) {
            if (board[row][col-1].equals("_") && board[row][col-2].equals("_")) {
                //possible hatzraha:
                Square newSquare = new Square(row, col - 2);
                possibleSquares.add(newSquare);
            }
        }
        return possibleSquares;
    }
}
