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
            checkAndAddPossibleSquare(board, possibleSquares, row - 1, col);
            if (stillNotMoved && board[row - 1][col].equals("_")) {
                checkAndAddPossibleSquare(board, possibleSquares, row - 2, col);
            }
        }
        else { //isUp
            checkAndAddPossibleSquare(board, possibleSquares, row + 1, col);
            if (stillNotMoved && board[row - 1][col].equals("_")) {
                checkAndAddPossibleSquare(board, possibleSquares, row + 2, col);
            }
        }
        return possibleSquares;
    }
}
